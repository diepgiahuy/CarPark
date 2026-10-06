import bpy, math, os
from mathutils import Vector

OUT = os.path.join(os.path.dirname(__file__), 'artifacts')
os.makedirs(OUT, exist_ok=True)

# ---------- reset ----------
bpy.ops.object.select_all(action='SELECT')
bpy.ops.object.delete(use_global=False)
for datablocks in (bpy.data.meshes, bpy.data.curves, bpy.data.materials, bpy.data.cameras, bpy.data.lights):
    pass

scene = bpy.context.scene
scene.render.engine = 'BLENDER_EEVEE_NEXT'
scene.render.resolution_x = 540
scene.render.resolution_y = 960
scene.render.resolution_percentage = 100
scene.render.image_settings.file_format = 'PNG'
scene.render.film_transparent = False
scene.render.fps = 30
scene.render.image_settings.color_mode = 'RGBA'
scene.render.resolution_percentage = 100
scene.render.use_file_extension = True
scene.render.engine = 'BLENDER_EEVEE_NEXT'
scene.render.image_settings.color_mode = 'RGB'
scene.render.image_settings.color_depth = '8'
scene.view_settings.look = 'AgX - Medium High Contrast'
scene.world.color = (0.015, 0.02, 0.028)

# ---------- helpers ----------
def mat(name, color, metallic=0.0, rough=0.5, emission=None, emission_strength=0.0):
    m = bpy.data.materials.new(name)
    m.use_nodes = True
    bsdf = m.node_tree.nodes.get('Principled BSDF')
    bsdf.inputs['Base Color'].default_value = color
    bsdf.inputs['Metallic'].default_value = metallic
    bsdf.inputs['Roughness'].default_value = rough
    if emission is not None:
        bsdf.inputs['Emission Color'].default_value = emission
        bsdf.inputs['Emission Strength'].default_value = emission_strength
    return m

def add_box(name, loc, scale, material, parent=None, bevel=0.12):
    bpy.ops.mesh.primitive_cube_add(location=loc)
    o = bpy.context.object
    o.name = name
    o.scale = scale
    bpy.ops.object.transform_apply(location=False, rotation=False, scale=True)
    if material:
        o.data.materials.append(material)
    if bevel > 0:
        bev = o.modifiers.new('Bevel', 'BEVEL')
        bev.width = bevel
        bev.segments = 2
    if parent:
        o.parent = parent
    return o

def look_at(obj, target):
    direction = Vector(target) - obj.location
    obj.rotation_euler = direction.to_track_quat('-Z', 'Y').to_euler()

def add_cylinder(name, loc, radius, depth, material, rot=(0,0,0), parent=None, vertices=24):
    bpy.ops.mesh.primitive_cylinder_add(vertices=vertices, radius=radius, depth=depth, location=loc, rotation=rot)
    o = bpy.context.object
    o.name = name
    if material:
        o.data.materials.append(material)
    if parent:
        o.parent = parent
    return o

def add_text(name, text, loc, scale, material, rot=(math.radians(90),0,0), parent=None):
    bpy.ops.object.text_add(location=loc, rotation=rot)
    o = bpy.context.object
    o.name = name
    o.data.body = text
    o.data.align_x = 'CENTER'
    o.data.align_y = 'CENTER'
    o.data.size = scale
    o.data.extrude = 0.015
    if material:
        o.data.materials.append(material)
    if parent:
        o.parent = parent
    return o

# ---------- materials ----------
steel = mat('HullSteel', (0.055,0.075,0.09,1), metallic=0.55, rough=0.32)
deck_mat = mat('Deck', (0.12,0.16,0.17,1), metallic=0.35, rough=0.5)
frame_mat = mat('BridgeFrame', (0.018,0.024,0.03,1), metallic=0.45, rough=0.25)
console_mat = mat('Console', (0.025,0.035,0.045,1), metallic=0.2, rough=0.32)
screen_mat = mat('Screen', (0.02,0.22,0.28,1), metallic=0.0, rough=0.16, emission=(0.02,0.45,0.62,1), emission_strength=2.2)
red_light = mat('Alarm', (0.28,0.01,0.01,1), rough=0.25, emission=(1.0,0.015,0.005,1), emission_strength=3.5)
white = mat('WhitePaint', (0.72,0.76,0.78,1), metallic=0.15, rough=0.38)
black = mat('Black', (0.008,0.01,0.012,1), metallic=0.1, rough=0.28)
foam = mat('Foam', (0.72,0.84,0.9,1), rough=0.18, emission=(0.04,0.06,0.07,1), emission_strength=0.4)
ocean_mat = mat('Ocean', (0.008,0.055,0.09,1), metallic=0.05, rough=0.18)
container_mats = [
    mat('ContRed', (0.45,0.035,0.018,1), metallic=0.2, rough=0.42),
    mat('ContBlue', (0.018,0.13,0.30,1), metallic=0.2, rough=0.40),
    mat('ContOrange', (0.62,0.20,0.018,1), metallic=0.18, rough=0.42),
    mat('ContGreen', (0.03,0.22,0.14,1), metallic=0.18, rough=0.44),
    mat('ContGray', (0.25,0.29,0.31,1), metallic=0.25, rough=0.38),
]

# ---------- ship rig ----------
bpy.ops.object.empty_add(type='PLAIN_AXES', location=(0,0,0))
ship = bpy.context.object
ship.name = 'SHIP_ROOT'

# hull/deck seen from bridge
add_box('MainDeck', (0,24,-0.4), (8.6,30,0.55), deck_mat, ship, 0.25)
# bow wedge mesh
verts=[(-8.2,45,-0.2),(8.2,45,-0.2),(-6.0,58,-0.2),(6.0,58,-0.2),(-8.2,45,1.3),(8.2,45,1.3),(-6.0,58,1.3),(6.0,58,1.3)]
faces=[(0,1,3,2),(4,6,7,5),(0,4,5,1),(2,3,7,6),(0,2,6,4),(1,5,7,3)]
mesh=bpy.data.meshes.new('BowMesh')
mesh.from_pydata(verts,[],faces); mesh.update()
bow=bpy.data.objects.new('Bow',mesh); bpy.context.collection.objects.link(bow); bow.data.materials.append(steel); bow.parent=ship
bev=bow.modifiers.new('BowBevel','BEVEL'); bev.width=0.25; bev.segments=2

# deck rails
for sx in (-8.3,8.3):
    add_box(f'RailBase{sx}', (sx,27,0.75), (0.06,26,0.06), white, ship, 0.03)
    for y in range(5,56,5):
        add_box(f'RailPost{sx}_{y}', (sx,y,1.1), (0.04,0.04,0.45), white, ship, 0.02)

# container stacks - long ocean-going ship read
for row,y in enumerate([8,15,22,29,36,43]):
    for col,x in enumerate([-5.6,-2.8,0,2.8,5.6]):
        height = 3 if row < 4 else 2
        for level in range(height):
            m = container_mats[(row+col+level)%len(container_mats)]
            z = 1.45 + level*2.45
            c = add_box(f'C_{row}_{col}_{level}', (x,y,z), (1.25,2.7,1.08), m, ship, 0.11)
            # corrugation impression on container end
            if level==0 and col in (0,4):
                for rx in (-0.7,0,0.7):
                    add_box('rib', (x+rx,y-2.72,z), (0.045,0.035,0.90), black, ship, 0.01)

# hero container, separate for movement
hero = add_box('HeroContainer', (-5.6,9,8.1), (1.25,2.7,1.08), container_mats[2], ship, 0.1)

# ---------- bridge interior based on real marine bridge proportions ----------
# floor/ceiling/sidebar only; front stays open through windows
add_box('BridgeFloor', (0,-7,0.0), (9,7,0.25), deck_mat, ship, 0.08)
add_box('BridgeCeiling', (0,-6.8,8.6), (9,7,0.25), frame_mat, ship, 0.08)
add_box('LeftWall', (-8.8,-7,4.3), (0.25,7,4.3), frame_mat, ship, 0.08)
add_box('RightWall', (8.8,-7,4.3), (0.25,7,4.3), frame_mat, ship, 0.08)
# forward window posts, slightly chunky like commercial ship bridge
for x in (-8.4,-5.6,-2.8,0,2.8,5.6,8.4):
    p=add_box(f'WindowPost_{x}', (x,0.1,4.55), (0.13,0.18,4.05), frame_mat, ship, 0.05)
    p.rotation_euler[1]=math.radians(-3)
add_box('WindowTop', (0,0.1,8.25), (8.7,0.2,0.22), frame_mat, ship, 0.05)
add_box('WindowBottom', (0,0.2,0.85), (8.7,0.25,0.28), frame_mat, ship, 0.06)
# bridge console
add_box('ConsoleBase', (0,-2.7,1.15), (6.9,1.65,1.05), console_mat, ship, 0.15)
add_box('ConsoleSlope', (0,-1.55,2.15), (6.7,0.75,0.28), console_mat, ship, 0.1).rotation_euler[0]=math.radians(-16)
# monitors
for i,x in enumerate((-4.4,-2.1,0,2.1,4.4)):
    mon=add_box(f'Monitor{i}', (x,-1.03,2.65), (0.72,0.10,0.48), black, ship, 0.06)
    scr=add_box(f'Screen{i}', (x,-0.91,2.65), (0.62,0.025,0.38), screen_mat, ship, 0.02)
# wheel + throttle feel
add_cylinder('WheelHub',(0,-1.7,2.0),0.22,0.32,black,rot=(math.radians(90),0,0),parent=ship)
for a in range(0,360,45):
    r=math.radians(a)
    add_box('WheelSpoke',(math.sin(r)*0.65,-1.45,2.0+math.cos(r)*0.65),(0.05,0.05,0.42),black,ship,0.02).rotation_euler[1]=r
add_box('Throttle', (5.8,-2.1,2.0), (0.18,0.18,0.7), black, ship, 0.05).rotation_euler[0]=math.radians(-18)
# alarm lamps
for x in (-6.2,6.2): add_box('AlarmLamp',(x,-0.9,7.55),(0.28,0.12,0.16),red_light,ship,0.06)
# wipers on front windows
for x in (-4.2,4.2):
    w=add_box('Wiper',(x,-0.03,4.45),(0.055,0.045,2.15),black,ship,0.02)
    w.rotation_euler[1]=math.radians(18 if x<0 else -18)

# ---------- ocean mesh ----------
N=70; size=180.0
verts=[]; faces=[]
for iy in range(N):
    y=-25 + iy*(size/(N-1))
    for ix in range(N):
        x=-90 + ix*(180/(N-1))
        z=-2.0 + 0.35*math.sin(x*0.16+y*0.11) + 0.18*math.sin(x*0.07-y*0.23)
        verts.append((x,y,z))
for iy in range(N-1):
    for ix in range(N-1):
        a=iy*N+ix; b=a+1; c=a+N+1; d=a+N
        faces.append((a,b,c,d))
mesh=bpy.data.meshes.new('OceanMesh'); mesh.from_pydata(verts,[],faces); mesh.update()
ocean=bpy.data.objects.new('Ocean',mesh); bpy.context.collection.objects.link(ocean); ocean.data.materials.append(ocean_mat)
sub=ocean.modifiers.new('OceanSubd','SUBSURF'); sub.levels=1; sub.render_levels=1

# Hero wave as a broad curved ridge sheet
Nw=42; Mw=22
wv=[]; wf=[]
for iy in range(Mw):
    yy=20 + iy*2.0
    profile=math.exp(-((yy-43.0)/8.0)**2)
    for ix in range(Nw):
        xx=-24 + ix*(48/(Nw-1))
        z=-1.6 + 8.0*profile*(0.92+0.08*math.cos(xx*0.18))
        # curl forward near crest
        ycurl=yy - 2.0*profile
        wv.append((xx,ycurl,z))
for iy in range(Mw-1):
    for ix in range(Nw-1):
        a=iy*Nw+ix; wf.append((a,a+1,a+Nw+1,a+Nw))
wm=bpy.data.meshes.new('HeroWaveMesh'); wm.from_pydata(wv,[],wf); wm.update()
wave=bpy.data.objects.new('HeroWave',wm); bpy.context.collection.objects.link(wave); wave.data.materials.append(ocean_mat)
solid=wave.modifiers.new('WaveSolid','SOLIDIFY'); solid.thickness=0.22
sub=wave.modifiers.new('WaveSubd','SUBSURF'); sub.levels=1; sub.render_levels=1
# foam crest strip
add_box('WaveFoam', (0,40.8,6.0), (22,1.4,0.35), foam, None, 0.25)

# ---------- camera inside bridge ----------
bpy.ops.object.camera_add(location=(0,-11.7,5.25))
cam=bpy.context.object
cam.name='Camera_POV'
cam.data.lens=25
cam.data.sensor_width=36
cam.parent=ship
look_at(cam,(0,31,2.2))
scene.camera=cam

# ---------- lights ----------
bpy.ops.object.light_add(type='SUN', location=(0,0,25))
sun=bpy.context.object; sun.data.energy=2.2; sun.data.angle=math.radians(18); sun.rotation_euler=(math.radians(38),math.radians(-18),math.radians(-25))
bpy.ops.object.light_add(type='AREA', location=(0,-7,7.2))
area=bpy.context.object; area.data.energy=700; area.data.shape='RECTANGLE'; area.data.size=10; area.data.size_y=4; area.data.color=(0.11,0.17,0.22); area.rotation_euler=(math.radians(90),0,0)

# ---------- animation keys ----------
def key_obj(obj, frame, loc=None, rot=None):
    if loc is not None:
        obj.location=loc; obj.keyframe_insert(data_path='location',frame=frame)
    if rot is not None:
        obj.rotation_euler=rot; obj.keyframe_insert(data_path='rotation_euler',frame=frame)

poses=[
    (1,  (0,0,0),                      (math.radians(-1),0,math.radians(-1))),
    (30, (0,0,0.35),                   (math.radians(2.2),0,math.radians(2.5))),
    (60, (0,0,-0.35),                  (math.radians(-3.4),0,math.radians(-5.0))),
    (90, (0,0,0.10),                   (math.radians(3.0),0,math.radians(7.0))),
    (120,(0,0,-0.45),                  (math.radians(-2.2),0,math.radians(-8.0))),
]
for f,loc,rot in poses: key_obj(ship,f,loc,rot)
# hero container breaks loose late
key_obj(hero,1,(-5.6,9,8.1),(0,0,0))
key_obj(hero,60,(-5.6,9,8.1),(0,0,0))
key_obj(hero,90,(-3.8,5.7,7.5),(math.radians(8),math.radians(-4),math.radians(18)))
key_obj(hero,120,(-1.5,1.4,6.3),(math.radians(16),math.radians(-8),math.radians(34)))
# wave approaches camera/ship
key_obj(wave,1,(0,22,0))
key_obj(wave,60,(0,5,0))
key_obj(wave,90,(0,-4,0))
key_obj(wave,120,(0,-11,0))
foam_obj=bpy.data.objects.get('WaveFoam')
key_obj(foam_obj,1,(0,62.8,6.0))
key_obj(foam_obj,60,(0,45.8,6.0))
key_obj(foam_obj,90,(0,36.8,6.0))
key_obj(foam_obj,120,(0,29.8,6.0))

# ---------- render 5 proof frames ----------
frames=[1,30,60,90,120]
for f in frames:
    scene.frame_set(f)
    scene.render.filepath=os.path.join(OUT,f'poc_{f:03d}.png')
    bpy.ops.render.render(write_still=True)

bpy.ops.wm.save_as_mainfile(filepath=os.path.join(OUT,'THE_NEXT_WAVE_POC.blend'))
print('POC_RENDER_PASS')
