import bpy, math, os
from mathutils import Vector

ROOT = os.path.dirname(__file__)
OUT = os.path.join(ROOT, 'artifacts')
os.makedirs(OUT, exist_ok=True)

# ---------------- reset ----------------
bpy.ops.object.select_all(action='SELECT')
bpy.ops.object.delete(use_global=False)

scene = bpy.context.scene
scene.render.engine = 'BLENDER_EEVEE'
scene.render.fps = 30
scene.render.resolution_percentage = 100
scene.render.image_settings.file_format = 'PNG'
scene.render.image_settings.color_mode = 'RGB'
scene.render.image_settings.color_depth = '8'
scene.world.color = (0.025, 0.035, 0.05)

# ---------------- helpers ----------------
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

def box(name, loc, scale, material=None, parent=None, bevel=0.08):
    bpy.ops.mesh.primitive_cube_add(location=loc)
    o = bpy.context.object
    o.name = name
    o.scale = scale
    bpy.ops.object.transform_apply(location=False, rotation=False, scale=True)
    if material:
        o.data.materials.append(material)
    if bevel:
        mod = o.modifiers.new('Bevel', 'BEVEL')
        mod.width = bevel
        mod.segments = 2
    if parent:
        o.parent = parent
    return o

def cyl(name, loc, radius, depth, material=None, parent=None, rot=(0,0,0), verts=24):
    bpy.ops.mesh.primitive_cylinder_add(vertices=verts, radius=radius, depth=depth, location=loc, rotation=rot)
    o = bpy.context.object
    o.name = name
    if material:
        o.data.materials.append(material)
    if parent:
        o.parent = parent
    return o

def empty(name, loc=(0,0,0), parent=None, size=4.0):
    bpy.ops.object.empty_add(type='PLAIN_AXES', location=loc)
    o = bpy.context.object
    o.name = name
    o.empty_display_size = size
    if parent:
        o.parent = parent
    return o

def look_at(obj, target):
    d = Vector(target) - obj.matrix_world.translation
    obj.rotation_euler = d.to_track_quat('-Z', 'Y').to_euler()

def parent_keep_world(child, parent):
    mw = child.matrix_world.copy()
    child.parent = parent
    child.matrix_world = mw

def add_driver(obj, data_path, index, source, prop, scale=1.0):
    fc = obj.driver_add(data_path, index)
    drv = fc.driver
    drv.type = 'SCRIPTED'
    var = drv.variables.new()
    var.name = 'v'
    var.targets[0].id = source
    var.targets[0].data_path = f'["{prop}"]'
    drv.expression = f'v*{scale}'
    return fc

# ---------------- materials ----------------
hull_blue = mat('Hull Navy', (0.018,0.055,0.085,1), metallic=0.55, rough=0.33)
hull_red = mat('Anti Fouling', (0.28,0.018,0.012,1), metallic=0.25, rough=0.5)
deck = mat('Deck Green', (0.055,0.14,0.12,1), metallic=0.25, rough=0.52)
white = mat('Superstructure', (0.72,0.76,0.78,1), metallic=0.08, rough=0.42)
dark = mat('Dark Metal', (0.012,0.018,0.024,1), metallic=0.55, rough=0.28)
glass = mat('Bridge Glass', (0.015,0.055,0.075,1), metallic=0.05, rough=0.12)
console = mat('Console', (0.035,0.055,0.062,1), metallic=0.22, rough=0.33)
screen = mat('Nav Screen', (0.01,0.18,0.22,1), rough=0.16, emission=(0.015,0.35,0.46,1), emission_strength=2.0)
wood = mat('Bridge Floor', (0.22,0.10,0.035,1), rough=0.46)
orange = mat('Safety Orange', (0.8,0.12,0.015,1), rough=0.42)
ocean_mat = mat('Ocean', (0.006,0.035,0.06,1), metallic=0.02, rough=0.16)
foam_mat = mat('Foam', (0.7,0.82,0.88,1), rough=0.25)
container_mats = [
    mat('Container Red', (0.42,0.025,0.018,1), metallic=0.24, rough=0.42),
    mat('Container Blue', (0.02,0.11,0.28,1), metallic=0.22, rough=0.4),
    mat('Container Orange', (0.65,0.16,0.02,1), metallic=0.2, rough=0.44),
    mat('Container Green', (0.02,0.25,0.14,1), metallic=0.2, rough=0.45),
    mat('Container Gray', (0.26,0.29,0.30,1), metallic=0.25, rough=0.39),
    mat('Container Beige', (0.46,0.42,0.31,1), metallic=0.16, rough=0.48),
]

# ---------------- collection hierarchy ----------------
rig_col = bpy.data.collections.new('RIG_CONTROLS')
geo_col = bpy.data.collections.new('SHIP_GEOMETRY')
bridge_col = bpy.data.collections.new('BRIDGE_INTERIOR')
cargo_col = bpy.data.collections.new('CARGO')
scene.collection.children.link(rig_col)
scene.collection.children.link(geo_col)
scene.collection.children.link(bridge_col)
scene.collection.children.link(cargo_col)

# controllers
master = empty('CTRL_MASTER', size=11)
heave = empty('CTRL_HEAVE', parent=master, size=9)
attitude = empty('CTRL_PITCH_ROLL', parent=heave, size=8)
bridge_ctrl = empty('CTRL_BRIDGE', parent=attitude, size=6)
cargo_ctrl = empty('CTRL_CARGO', parent=attitude, size=6)
cam_ctrl = empty('CTRL_CAMERA_INERTIA', parent=bridge_ctrl, size=3)
wiper_ctrl = empty('CTRL_WIPERS', parent=bridge_ctrl, size=2)
hero_ctrl = empty('CTRL_HERO_CONTAINER', parent=cargo_ctrl, size=3)

# rig custom properties
master['heave_m'] = 0.0
master['pitch_deg'] = 0.0
master['roll_deg'] = 0.0
master['yaw_deg'] = 0.0
master['cargo_sway_deg'] = 0.0
hero_ctrl['slide_m'] = 0.0
hero_ctrl['tip_deg'] = 0.0
wiper_ctrl['sweep_deg'] = 0.0

add_driver(heave, 'location', 2, master, 'heave_m', 1.0)
add_driver(attitude, 'rotation_euler', 0, master, 'pitch_deg', math.pi/180.0)
add_driver(attitude, 'rotation_euler', 1, master, 'roll_deg', math.pi/180.0)
add_driver(attitude, 'rotation_euler', 2, master, 'yaw_deg', math.pi/180.0)
add_driver(cargo_ctrl, 'rotation_euler', 1, master, 'cargo_sway_deg', math.pi/180.0)
add_driver(hero_ctrl, 'location', 0, hero_ctrl, 'slide_m', 1.0)
add_driver(hero_ctrl, 'rotation_euler', 1, hero_ctrl, 'tip_deg', math.pi/180.0)

# ---------------- hull: lofted ocean-going container ship ----------------
sections = [
    (-182, 4.0), (-170, 12.0), (-145, 22.0), (-100, 25.5), (-40, 26.0),
    (30, 26.0), (95, 25.5), (135, 23.0), (160, 16.0), (176, 6.0), (182, 0.8)
]
verts=[]
# per section: top L/R, water L/R, keel L/R
for y, hw in sections:
    lower = max(0.7, hw*0.72)
    keel = max(0.35, hw*0.20)
    verts += [(-hw,y,10),(hw,y,10),(-lower,y,0),(lower,y,0),(-keel,y,-12),(keel,y,-12)]
faces=[]
for i in range(len(sections)-1):
    a=i*6; b=(i+1)*6
    faces += [
        (a+0,a+1,b+1,b+0),
        (a+0,b+0,b+2,a+2),
        (a+1,a+3,b+3,b+1),
        (a+2,b+2,b+4,a+4),
        (a+3,a+5,b+5,b+3),
        (a+4,b+4,b+5,a+5)
    ]
faces += [(0,2,4,5,3,1)]
end=(len(sections)-1)*6
faces += [(end+0,end+1,end+3,end+5,end+4,end+2)]
mesh=bpy.data.meshes.new('MegaContainerHullMesh'); mesh.from_pydata(verts,[],faces); mesh.update()
hull=bpy.data.objects.new('HULL_MAIN',mesh); geo_col.objects.link(hull); hull.data.materials.append(hull_blue); hull.parent=attitude
bev=hull.modifiers.new('Hull Edge Softening','BEVEL'); bev.width=0.35; bev.segments=2

# red lower hull band as separate slim hull approximation
red_band = box('HULL_RED_BAND',(0,-3,-5.5),(25.3,165,5.5),hull_red,attitude,0.4)
# keep red band mostly buried inside loft, only waterline/lower side visible in exterior view

# deck and bow details
box('MAIN_DECK',(0,-1,10.35),(25.5,164,0.35),deck,attitude,0.18)
box('FORECASTLE',(0,150,12.0),(17.0,20,1.5),deck,attitude,0.2)
# bulbous bow
bpy.ops.mesh.primitive_uv_sphere_add(segments=32, ring_count=16, location=(0,176,-5.0), scale=(5.0,12.0,4.2))
bulb=bpy.context.object; bulb.name='BULBOUS_BOW'; bulb.data.materials.append(hull_red); bulb.parent=attitude

# stern deck step
box('STERN_DECK',(0,-145,12.2),(23,25,1.8),deck,attitude,0.3)

# rails, mooring gear, vents
for sx in (-24.5,24.5):
    box(f'RAIL_TOP_{sx}',(sx,10,11.6),(0.08,145,0.08),white,attitude,0.02)
    for y in range(-125,146,15):
        box(f'RAIL_POST_{sx}_{y}',(sx,y,11.1),(0.055,0.055,0.55),white,attitude,0.02)
for y in (-140,140):
    for x in (-13,13):
        cyl(f'BOLLARD_{x}_{y}',(x,y,11.25),0.55,1.2,dark,attitude)
        cyl(f'WINCH_{x}_{y}',(x,y+5,11.5),1.2,2.2,dark,attitude,rot=(math.radians(90),0,0))

# ---------------- cargo stacks ----------------
# dimensions ~40ft container scaled in meters
CW, CL, CH = 2.35, 11.8, 2.45
bay_ys = [-96,-80,-64,-48,-32,-16,0,16,32,48,64,80,96,112]
col_xs = [-20.0,-15.0,-10.0,-5.0,0,5.0,10.0,15.0,20.0]
container_objects=[]
for bi,y in enumerate(bay_ys):
    # leave foredeck lower and slight gap around bridge sightline
    max_levels = 4 if y < 80 else 3
    for ci,x in enumerate(col_xs):
        levels=max_levels
        if abs(x)>16 and bi%3==0: levels=max(2,levels-1)
        for li in range(levels):
            z=11.9 + CH/2 + li*(CH+0.18)
            c=box(f'CONT_B{bi:02d}_C{ci:02d}_L{li}',(x,y,z),(CW/2,CL/2,CH/2),container_mats[(bi+ci+li)%len(container_mats)],cargo_ctrl,0.07)
            container_objects.append(c)
            # end ribs only on visible top/edge containers
            if li==levels-1 and (ci in (0,4,8)):
                for rx in (-0.72,-0.36,0,0.36,0.72):
                    rib=box('container_rib',(x+rx,y-CL/2-0.04,z),(0.035,0.05,CH*0.42),dark,cargo_ctrl,0.01)

# lashing bridges between selected bays
for y in (-88,-56,-24,8,40,72):
    box(f'LASHING_BRIDGE_{y}',(0,y,12.8),(23.2,0.35,1.2),dark,cargo_ctrl,0.05)

# hero loose container, placed near aft top stack
hero = box('HERO_LOOSE_CONTAINER',(15,-82,24.2),(CW/2,CL/2,CH/2),container_mats[2],hero_ctrl,0.08)
# local hero controller origin at its base position
hero_ctrl.location=(0,0,0)

# ---------------- superstructure / bridge exterior ----------------
# aft white accommodation block inspired by modern mega container ships
box('SUPERSTRUCTURE_BASE',(0,-132,18.5),(20,20,8.0),white,bridge_ctrl,0.35)
box('SUPERSTRUCTURE_MID',(0,-127,27.0),(21,14,3.2),white,bridge_ctrl,0.3)
box('BRIDGE_HOUSE',(0,-116,33.0),(23,7.0,3.8),white,bridge_ctrl,0.28)
# bridge front dark panoramic windows
for x in (-18,-12,-6,0,6,12,18):
    box(f'EXT_BRIDGE_GLASS_{x}',(x,-108.82,33.8),(2.5,0.16,2.0),glass,bridge_ctrl,0.06)
for x in (-21,-15,-9,-3,3,9,15,21):
    box(f'EXT_WINDOW_POST_{x}',(x,-108.6,33.8),(0.16,0.26,2.2),dark,bridge_ctrl,0.04)
# wings
box('BRIDGE_WING_L',(-27,-117,31.5),(5.0,7.5,0.5),white,bridge_ctrl,0.12)
box('BRIDGE_WING_R',(27,-117,31.5),(5.0,7.5,0.5),white,bridge_ctrl,0.12)
# bridge wing rails
for sx in (-31,31):
    box(f'WING_RAIL_{sx}',(sx,-117,33.0),(0.07,7,0.07),white,bridge_ctrl,0.02)
# mast / radars
box('MAST',(0,-128,43.0),(0.5,0.5,7.0),dark,bridge_ctrl,0.05)
box('MAST_YARD',(0,-128,47.5),(10,0.25,0.25),dark,bridge_ctrl,0.04)
for x in (-8,8): cyl(f'RADAR_{x}',(x,-128,48.1),0.25,8.0,white,bridge_ctrl,rot=(0,math.radians(90),0))
# funnel and lifeboats
box('FUNNEL',(0,-151,31.0),(5,5,9),dark,bridge_ctrl,0.4)
for sx in (-1,1):
    b=box(f'LIFEBOAT_{sx}',(sx*22,-139,24.5),(2.3,6.0,2.0),orange,bridge_ctrl,0.5)
    b.rotation_euler[1]=math.radians(6*sx)

# ---------------- bridge interior shell ----------------
# camera room aligned to exterior front face
box('BR_INT_FLOOR',(0,-118,29.65),(21.5,8.7,0.22),wood,bridge_ctrl,0.08)
box('BR_INT_CEILING',(0,-118,37.2),(21.5,8.7,0.22),white,bridge_ctrl,0.08)
box('BR_INT_REAR_WALL',(0,-126.5,33.4),(21.5,0.22,3.7),white,bridge_ctrl,0.08)
# front window posts - window opening itself stays open for POV
for x in (-21,-15,-9,-3,3,9,15,21):
    box(f'BR_INT_POST_{x}',(x,-109.1,33.45),(0.17,0.22,3.6),dark,bridge_ctrl,0.04)
box('BR_INT_WINDOW_TOP',(0,-109.1,36.9),(22,0.24,0.2),dark,bridge_ctrl,0.05)
box('BR_INT_WINDOW_BOTTOM',(0,-109.1,30.25),(22,0.24,0.22),dark,bridge_ctrl,0.05)
# main navigation console
base=box('NAV_CONSOLE_BASE',(0,-112.6,30.8),(13.5,2.2,0.95),console,bridge_ctrl,0.22)
sl=box('NAV_CONSOLE_SLOPE',(0,-110.95,31.75),(13.2,0.9,0.24),console,bridge_ctrl,0.10); sl.rotation_euler[0]=math.radians(-14)
for i,x in enumerate((-10,-5,0,5,10)):
    box(f'NAV_MONITOR_{i}',(x,-110.3,32.95),(1.8,0.12,1.15),dark,bridge_ctrl,0.09)
    box(f'NAV_SCREEN_{i}',(x,-110.15,32.95),(1.55,0.035,0.92),screen,bridge_ctrl,0.03)
# central control pedestal / throttles
box('CENTER_PEDESTAL',(0,-115.0,31.2),(2.5,1.8,1.3),console,bridge_ctrl,0.18)
for x in (-0.7,0.7):
    lever=box(f'THROTTLE_{x}',(x,-113.8,33.0),(0.12,0.12,0.7),dark,bridge_ctrl,0.05); lever.rotation_euler[0]=math.radians(-18)
# captain/operator chairs
for x in (-5.5,5.5):
    box(f'CHAIR_SEAT_{x}',(x,-117.0,31.0),(1.25,1.25,0.28),dark,bridge_ctrl,0.15)
    box(f'CHAIR_BACK_{x}',(x,-118.0,32.3),(1.25,0.28,1.45),dark,bridge_ctrl,0.16)
    cyl(f'CHAIR_POST_{x}',(x,-117.0,30.2),0.18,1.2,dark,bridge_ctrl)
# side consoles
for sx in (-1,1):
    box(f'SIDE_CONSOLE_{sx}',(sx*17.5,-116.5,31.0),(3.0,5.0,1.0),console,bridge_ctrl,0.2)
    for j in range(3):
        box(f'SIDE_SCREEN_{sx}_{j}',(sx*17.5,-112.2-j*2.3,32.3),(1.3,0.08,0.75),screen,bridge_ctrl,0.04)
# overhead panel
box('OVERHEAD_PANEL',(0,-112.0,36.25),(8.0,1.0,0.45),console,bridge_ctrl,0.12)
for x in (-6,-3,0,3,6): box(f'OVERHEAD_SCREEN_{x}',(x,-110.95,35.9),(1.0,0.06,0.5),screen,bridge_ctrl,0.03)
# wipers
wipers=[]
for x in (-14,-7,0,7,14):
    w=box(f'WIPER_{x}',(x,-108.8,33.2),(0.045,0.05,2.5),dark,wiper_ctrl,0.015)
    w.rotation_euler[1]=math.radians(-18)
    wipers.append(w)
    add_driver(w,'rotation_euler',1,wiper_ctrl,'sweep_deg',math.pi/180.0)

# ---------------- ocean preview ----------------
N=80
size_x=320; y0=-220; size_y=480
ov=[]; of=[]
for iy in range(N):
    y=y0 + iy*(size_y/(N-1))
    for ix in range(N):
        x=-size_x/2 + ix*(size_x/(N-1))
        z=-0.8 + 0.65*math.sin(x*0.055+y*0.045) + 0.28*math.sin(x*0.12-y*0.07)
        ov.append((x,y,z))
for iy in range(N-1):
    for ix in range(N-1):
        a=iy*N+ix; of.append((a,a+1,a+N+1,a+N))
om=bpy.data.meshes.new('OceanMesh'); om.from_pydata(ov,[],of); om.update()
ocean=bpy.data.objects.new('OCEAN_PREVIEW',om); scene.collection.objects.link(ocean); ocean.data.materials.append(ocean_mat)
sub=ocean.modifiers.new('OceanSmooth','SUBSURF'); sub.levels=1; sub.render_levels=1

# ---------------- cameras ----------------
def new_camera(name, loc, target, lens, parent=None):
    bpy.ops.object.camera_add(location=loc)
    c=bpy.context.object; c.name=name; c.data.lens=lens; c.data.sensor_width=36
    if parent: parent_keep_world(c,parent)
    look_at(c,target)
    return c

cam_ext = new_camera('CAM_EXTERIOR',(-105,-235,80),(0,5,12),44,None)
cam_pov = new_camera('CAM_BRIDGE_POV',(0,-121.5,34.2),(0,115,13.0),25,cam_ctrl)
cam_side = new_camera('CAM_SIDE',(120,-40,48),(0,5,12),52,None)

# ---------------- lighting ----------------
bpy.ops.object.light_add(type='SUN', location=(0,0,150))
sun=bpy.context.object; sun.name='Storm_Key'; sun.data.energy=2.4; sun.data.angle=math.radians(22); sun.rotation_euler=(math.radians(38),math.radians(-20),math.radians(-28))
bpy.ops.object.light_add(type='AREA', location=(0,-116,39))
area=bpy.context.object; area.name='Bridge_Fill'; area.data.energy=1200; area.data.shape='RECTANGLE'; area.data.size=26; area.data.size_y=10; area.data.color=(0.16,0.23,0.28); area.rotation_euler=(math.radians(90),0,0); area.parent=bridge_ctrl

# ---------------- demo rig animation ----------------
# use custom properties so user can animate from one master controller
keys = [
    (1, 0.0, 0.0, 0.0, 0.0),
    (30, 0.5, 2.0, -3.0, 0.0),
    (60, -0.8, -4.0, 7.0, 0.6),
    (90, 0.6, 3.5, -6.0, -0.3),
    (120, -0.2, -1.0, 2.0, 0.0),
]
for f,h,p,r,yaw in keys:
    master['heave_m']=h; master.keyframe_insert(data_path='["heave_m"]',frame=f)
    master['pitch_deg']=p; master.keyframe_insert(data_path='["pitch_deg"]',frame=f)
    master['roll_deg']=r; master.keyframe_insert(data_path='["roll_deg"]',frame=f)
    master['yaw_deg']=yaw; master.keyframe_insert(data_path='["yaw_deg"]',frame=f)
# wiper sweep
for f,v in [(1,-18),(20,18),(40,-18),(60,18),(80,-18),(100,18),(120,-18)]:
    wiper_ctrl['sweep_deg']=v; wiper_ctrl.keyframe_insert(data_path='["sweep_deg"]',frame=f)
# hero container motion proof
for f,s,t in [(1,0,0),(50,0,0),(75,-3.0,4),(90,-7.0,12),(110,-10.0,20),(120,-10.0,20)]:
    hero_ctrl['slide_m']=s; hero_ctrl.keyframe_insert(data_path='["slide_m"]',frame=f)
    hero_ctrl['tip_deg']=t; hero_ctrl.keyframe_insert(data_path='["tip_deg"]',frame=f)

scene.frame_start=1; scene.frame_end=120

# ---------------- save ----------------
blend_path=os.path.join(OUT,'THE_NEXT_WAVE_ship_rig_v1.blend')
bpy.ops.wm.save_as_mainfile(filepath=blend_path)

# ---------------- preview renders ----------------
def render_still(cam, frame, path, w, h):
    scene.camera=cam
    scene.frame_set(frame)
    scene.render.resolution_x=w; scene.render.resolution_y=h
    scene.render.filepath=path
    bpy.ops.render.render(write_still=True)

render_still(cam_ext,1,os.path.join(OUT,'01_exterior.png'),640,360)
render_still(cam_pov,30,os.path.join(OUT,'02_bridge_pov.png'),360,640)
render_still(cam_pov,90,os.path.join(OUT,'03_rig_motion.png'),360,640)

# documentation
with open(os.path.join(OUT,'RIG_README.txt'),'w') as f:
    f.write('''THE NEXT WAVE - Generic Mega Container Ship Rig v1\n\nMAIN CONTROLS\nCTRL_MASTER custom properties:\n- heave_m\n- pitch_deg\n- roll_deg\n- yaw_deg\n- cargo_sway_deg\n\nCTRL_HERO_CONTAINER custom properties:\n- slide_m\n- tip_deg\n\nCTRL_WIPERS custom property:\n- sweep_deg\n\nCAMERAS\n- CAM_EXTERIOR\n- CAM_BRIDGE_POV\n- CAM_SIDE\n\nHierarchy is rigid-body style, intended for ship/ocean cinematics. Geometry is generic and contains no third-party branding.\n''')

print('SHIP_RIG_V1_PASS')
