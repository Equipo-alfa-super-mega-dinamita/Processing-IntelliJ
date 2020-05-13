import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import nub.primitives.*;
import nub.core.*;
import nub.processing.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class HelloWorldNub extends PApplet {

    /**
     * Cajas Orientadas.
     * by Jean Pierre Charalambos.
     *
     * This example illustrates some basic Node properties, particularly how to
     * orient them.
     *
     * The sphere and the boxes are interactive. Pick and drag them with the
     * right mouse button. Use also the arrow keys to select and move the sphere.
     * See how the boxes will always remain oriented towards the sphere.
     *
     * Press ' ' the change the picking policy adaptive/fixed.
     * Press 'c' to change the bullseye shape.
     */





    Scene scene;
    Box[] cajas;
    boolean drawAxes = true, bullseye = true;
    Sphere esfera;
    Vector orig = new Vector();
    Vector dir = new Vector();
    Vector end = new Vector();
    Vector pup;

    public void setup() {

        scene = new Scene(this);
        scene.setRadius(200);
        scene.togglePerspective();
        scene.fit();
        esfera = new Sphere(color(random(0, 255), random(0, 255), random(0, 255)), 10);
        esfera.setPosition(new Vector(0, 1.4f, 0));
        cajas = new Box[15];
        for (int i = 0; i < cajas.length; i++)
            cajas[i] = new Box(color(random(0, 255), random(0, 255), random(0, 255)),
                    random(10, 40), random(10, 40), random(10, 40));
        scene.fit();
        scene.tag("keyboard", esfera);
    }

    public void draw() {
        background(0);
        // calls render() on all scene nodes applying all their transformations
        scene.render();
        drawRay();
    }

    public void drawRay() {
        if (pup != null) {
            pushStyle();
            strokeWeight(20);
            stroke(255, 255, 0);
            point(pup.x(), pup.y(), pup.z());
            strokeWeight(8);
            stroke(0, 0, 255);
            line(orig.x(), orig.y(), orig.z(), end.x(), end.y(), end.z());
            popStyle();
        }
    }

    public void mouseClicked(MouseEvent event) {
        if (event.getButton() == RIGHT) {
            pup = scene.mouseLocation();
            if (pup != null) {
                scene.mouseToLine(orig, dir);
                end = Vector.add(orig, Vector.multiply(dir, 4000));
            }
        } else {
            scene.focusEye();
        }
    }

    public void mouseMoved() {
        scene.mouseTag();
    }

    public void mouseDragged() {
        if (mouseButton == LEFT)
            scene.mouseSpin();
        else if (mouseButton == RIGHT)
            scene.mouseTranslate();
        else
            scene.scale(mouseX - pmouseX);
    }

    public void mouseWheel(MouseEvent event) {
        scene.moveForward(event.getCount() * 20);
    }

    public int randomColor() {
        return color(random(0, 255), random(0, 255), random(0, 255));
    }

    public int randomLength(int min, int max) {
        return PApplet.parseInt(random(min, max));
    }

    public void keyPressed() {
        if (key == ' ')
            for (Box caja : cajas)
                if (caja.pickingThreshold() != 0)
                    if (abs(caja.pickingThreshold()) < 1)
                        caja.setPickingThreshold(100 * caja.pickingThreshold());
                    else
                        caja.setPickingThreshold(caja.pickingThreshold() / 100);
        if (key == 'c')
            for (Box caja : cajas)
                caja.setPickingThreshold(-1 * caja.pickingThreshold());
        if (key == 'a')
            drawAxes = !drawAxes;
        if (key == 'p')
            bullseye = !bullseye;
        if (key == 'e')
            scene.togglePerspective();
        if (key == 's')
            scene.fit(1);
        if (key == 'S')
            scene.fit();
        if (key == 'u')
            if (scene.isTagValid("keyboard"))
                scene.removeTag("keyboard");
            else
                scene.tag("keyboard", esfera);
        if (key == CODED)
            if (keyCode == UP)
                scene.translate("keyboard", 0, -10, 0);
            else if (keyCode == DOWN)
                scene.translate("keyboard", 0, 10, 0);
            else if (keyCode == LEFT)
                scene.translate("keyboard", -10, 0, 0);
            else if (keyCode == RIGHT)
                scene.translate("keyboard", 10, 0, 0);
    }
    public class Box extends Node {
        float _w, _h, _d;
        int _color;

        public Box(int tint, float w, float h, float d) {
            _color = tint;
            _w = w;
            _h = h;
            _d = d;
            setPickingThreshold(PApplet.max(_w, _h, _d)/scene.radius());
            scene.randomize(this);
        }

        // note that within render() geometry is defined
        // at the node local coordinate system
        @Override
        public void graphics(PGraphics pg) {
            pg.pushStyle();
            updateOrientation(esfera.position());
            if (drawAxes)
                Scene.drawAxes(pg, PApplet.max(_w, _h, _d) * 1.3f);
            pg.noStroke();
            pg.fill(isTagged(scene) ? color(255, 0, 0) : _color);
            pg.box(_w, _h, _d);
            pg.stroke(255);
            if (bullseye)
                scene.drawBullsEye(this);
            pg.popStyle();
        }

        public void updateOrientation(Vector v) {
            Vector to = Vector.subtract(v, position());
            setOrientation(new Quaternion(new Vector(0, 1, 0), to));
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
            if (drawAxes)
                Scene.drawAxes(pg, _radius * 1.3f);
            pg.noStroke();
            pg.fill(isTagged(scene) ? color(255, 0, 0) : _color);
            pg.sphere(isTagged(scene) ? _radius * 1.2f : _radius);
            pg.stroke(255);
            if (bullseye)
                scene.drawBullsEye(this);
            pg.popStyle();
        }
    }
    public void settings() {  size(800, 800, P3D); }
    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "HelloWorldNub" };
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}
