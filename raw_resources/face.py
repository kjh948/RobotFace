import pyglet
import glob

window = pyglet.window.Window(fullscreen=False)

text_label = pyglet.text.Label(text='test',x=0,y=1000)

def load_sprite(file_path):
    fnames = sorted(glob.glob(file_path+'*.png'))
    sprites = []
    for fname in fnames:
        sprites.append(pyglet.resource.image(fname))
    return sprites

path_base = 'res/robot/face/1/'
exp_set = ['face01','face02','face03','face04','face05','face06']

anim = dict()
fps = 10

for exp in exp_set:
    print 'loading '+exp
    sp = load_sprite(path_base+exp+'/')
    anim[exp] = pyglet.image.Animation.from_image_sequence(sp,1./fps,True)

sprite = pyglet.sprite.Sprite(anim['face01'])
sprite.scale = 3.8

ichange = 0

@window.event
def on_key_press(symbol,modifiers):
    global ichange
    id = ichange%len(exp_set)
    sprite.image = anim[exp_set[id]]
    ichange += 1
@window.event
def on_draw():
    window.clear()
    sprite.draw()
    id = ichange%len(exp_set)
    text_label.text = str(pyglet.clock.get_fps()) + '\t'+exp_set[id]
    print str(pyglet.clock.get_fps()) + '\t'+exp_set[id]
    text_label.draw()


if __name__ == '__main__':
    pyglet.app.run()

