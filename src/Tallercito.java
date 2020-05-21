import processing.core.PApplet;
import nub.primitives.*;
import nub.core.*;
import nub.processing.*;
import processing.core.*;
import processing.event.MouseEvent;


public class Tallercito extends PApplet {

    /**
     * Mini Map
     * by Jean Pierre Charalambos.
     *
     * This example illustrates how to use off-screen rendering to build
     * a mini-map of the main Scene where all objects are interactive.
     * Note that the minimap displays the projection of the scene onto
     * the near plane in 3D.
     *
     * Press ' ' to toggle the minimap display.
     * Press 'i' to toggle the interactivity of the minimap scene eye.
     * Press 'f' to show the entire scene or minimap.
     * Press 't' to toggle the scene camera type (only in 3D).
     */

    Scene scene, minimap, focus;
    Sphere esfera;

    boolean displayMinimap = true;
    // whilst scene is either on-screen or not, the minimap is always off-screen
// test both cases here:
    boolean onScreen = true;
    boolean interactiveEye;

    int w = 500;
    int h = 500;

    //Choose P2D or P3D
    String renderer = P2D;

    public void settings() {
        size(w, h, renderer);
    }

    public void setup() {
        Node eye = new Node() {
            @Override
            public void graphics(PGraphics pg) {
                pg.pushStyle();
                pg.fill(isTagged(minimap) ? 255 : 25, isTagged(minimap) ? 0 : 255, 255, 125);
                pg.strokeWeight(2);
                pg.stroke(0, 0, 255);
                Scene.drawFrustum(pg, scene);
                pg.popStyle();
            }
        };
        eye.setPickingThreshold(50);
        eye.setHighlighting(0);
        scene = onScreen ? new Scene(this, eye) : new Scene(this, renderer, eye);
        scene.setRadius(1000);
        rectMode(CENTER);
        scene.fit(1);

        // Note that we pass the upper left corner coordinates where the minimap
        // is to be drawn (see drawing code below) to its constructor.
        minimap = new Scene(this, renderer, w / 2, h / 2, w / 2, h / 2);
        minimap.setRadius(2000);
        if (renderer == P3D)
            minimap.togglePerspective();
        minimap.fit(1);

        esfera = new Sphere(color(random(0, 255)),5);
    }


    public void keyPressed() {
        if (key == ' ')
            displayMinimap = !displayMinimap;
        if (key == 'i') {
            interactiveEye = !interactiveEye;
            if (interactiveEye)
                minimap.tag(scene.eye());
            else
                minimap.untag(scene.eye());
        }
        if (key == 'f')
            focus.fit(1);
        if (key == 't')
            focus.togglePerspective();
    }

    public void mouseMoved() {
        if (!interactiveEye || focus == scene)
            focus.mouseTag();
    }

    public void mouseDragged() {
        if (mouseButton == LEFT)
            focus.mouseSpin();
        else if (mouseButton == RIGHT)
            focus.mouseTranslate();
        else
            focus.scale(focus.mouseDX());
    }

    public void mouseWheel(MouseEvent event) {
        if (renderer == P3D)
            focus.moveForward(event.getCount() * 40);
        else
            focus.scale(event.getCount() * 40);
    }

    public void mouseClicked(MouseEvent event) {
        if (event.getCount() == 2)
            if (event.getButton() == LEFT)
                focus.focus();
            else
                focus.align();
    }

    public void draw() {
        focus = displayMinimap ? (mouseX > w / 2 && mouseY > h / 2) ? minimap : scene : scene;
        background(75, 25, 15);
        if (scene.isOffscreen()) {
            scene.beginDraw();
            scene.context().background(75, 25, 15);
            scene.drawAxes();
            scene.render();
            scene.endDraw();
            scene.display();
        } else {
            scene.drawAxes();
            scene.render();
        }
        if (displayMinimap) {
            if (!scene.isOffscreen())
                scene.beginHUD();
            minimap.beginDraw();
            minimap.context().background(125, 80, 90);
            minimap.drawAxes();
            minimap.render();
            minimap.context().stroke(255);
            minimap.drawBullsEye(scene.eye());
            minimap.endDraw();
            minimap.display();
            if (!scene.isOffscreen())
                scene.endHUD();
        }
    }


    class Sphere extends Node {
        float _radius;
        int _color;

        public Sphere(int tint, float radius) {
            _color = tint;
            _radius = radius;
        }

        @Override
        public void graphics(PGraphics pg) {
            pg.pushStyle();
            pg.noStroke();
            pg.fill(isTagged(scene) ? color(255, 0, 0) : _color);
            pg.sphere(isTagged(scene) ? _radius * 1.2f : _radius);
            pg.stroke(255);
            pg.popStyle();
        }
    }
    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "Tallercito" };
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}
