import nub.primitives.*;
import nub.core.*;
import nub.core.constraint.*;
import nub.processing.*;

//this packages are required for ik behavior
import nub.ik.animation.*;
import nub.ik.solver.*;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.event.MouseEvent;

import java.util.ArrayList;


public class Main<IKController> extends PApplet {

    public class IKController{
        public IKController(Skeleton sk){
            skeleton = sk;
            generateLookAtTarget(sk.scene().radius() * 0.05f);

            Limb limb1 = new Limb(sk, "Front Limb Left 3");
            Limb limb2 = new Limb(sk, "Front Limb Right 3");
            Limb limb3 = new Limb(sk, "Back Limb Left 3");
            Limb limb4 = new Limb(sk, "Back Limb Right 3");

            limbs.add(limb1);
            limbs.add(limb2);
            limbs.add(limb3);
            limbs.add(limb4);



            task = new TimingTask() {
                @Override
                public void execute() {
                    lookAt();
                    update();
                }
            };
            task.run(20);

        }
        class Limb{
            String name;
            Node node, idle, target, last;

            Interpolator interpolator;


            Limb(Skeleton skeleton, String name){
                this.name = name;
                node = skeleton.joint(name);
                PShape box = createShape(BOX, scene.radius() * 0.1f);
                box.setFill(color(0,255,255));
                idle = new Node(box);

                idle.setReference(skeleton.joint("Hips"));
                idle.setPosition(node.position());
                idle.setOrientation(node.orientation());
                idle.setPickingThreshold(0);

                target = skeleton.target(name);
                interpolator = new Interpolator(target);
            }

            void moveToIdle(){
                float duration = stepDuration / 3;
                interpolator.reset();
                interpolator.clear();
                //define 3 key frames to emulate a parabolic movement
                interpolator.addKeyFrame(target.detach(), duration); //current position
                //middle key frame
                Vector pos = Vector.add(idle.position().get(),node.position().get());
                pos.multiply(0.5f);
                pos.setY(pos.y() + stepHeight);
                Quaternion q = Quaternion.slerp(node.orientation().get(), idle.orientation().get(), 0.5f);
                Node middle = Node.detach(pos, q, node.magnitude());
                interpolator.addKeyFrame(middle, duration); //current position
                last = Node.detach(idle.position().get(), idle.orientation().get(), idle.magnitude());
                interpolator.addKeyFrame(last, duration);
                interpolator.run();

            }
            void update(){
                if(interpolator.task().isActive()){
                    return;
                }
                //check distance (only in XZ plane)
                Vector v1 = target.position().get();
                v1.setY(0);
                Vector v2 = idle.position().get();
                v2.setY(0);

                if(Vector.distance(v1 , v2) > distanceThreshold){
                    //enable stepping
                    moveToIdle();
                }
            }

        }
        float distanceThreshold = boneLength * 0.05f;
        float stepHeight = boneLength * 0.25f;
        float stepDuration = 0.3f;
        float speed = 10f;
        float turnSpeed = radians(3);

        int state = 0;

        Skeleton skeleton;
        Node lookAtTarget;

        ArrayList<Limb> limbs = new ArrayList<Limb>();

        TimingTask task;

        void generateLookAtTarget(float r){
            PShape box = createShape(BOX, r);
            lookAtTarget = new Node(box);
            lookAtTarget.setPickingThreshold(0);
            Node head = skeleton.joint("Head");
            lookAtTarget.set(head);
            lookAtTarget.translate(boneLength * 4,0,0);

        }

        //1. Reorient the head of the skeleton to look at an specific object
        void lookAt(){
            Node neck = skeleton.joint("Neck");
            Vector eyesDir = new Vector(1,0,0); //local coordinates w.r.t Neck
            Vector target = neck.location(lookAtTarget);
            //find the rotation required to Align the bone with the target
            Quaternion rotation = new Quaternion(eyesDir, target);
            //Apply the rotation to the neck
            neck.rotate(rotation, 0.3f);
        }

        void move(Vector v){
            v.normalize();
            v.multiply(speed);
            v = skeleton.joint("Hips").reference().displacement(v, skeleton.joint("Hips"));

            skeleton.joint("Hips").translate(v.x(), v.y(), v.z());
            skeleton.IKStatusChanged();
        }

        void turn(float angle){
            Quaternion rot = new Quaternion(new Vector(0,1,0), angle > 0 ? turnSpeed : -turnSpeed); //rotation is always along Y axis
            skeleton.disableConstraints();
            skeleton.joint("Hips").rotate(rot);
            skeleton.enableConstraints();
            skeleton.IKStatusChanged();
        }

        boolean updateState(){
            boolean update = true;
            for(Limb l : limbs){
                //if there is some interpolator doing work then wait
                if(l.interpolator.task().isActive()){
                    //update last keyFrame
                    l.last.setPosition(l.idle.position());
                    update = false;
                }
            }
            if(update) state = (state + 1) % 3;
            return update;
        }


        void update(){
            if(!updateState()) return;
            if(state == 1){
                limbs.get(0).update();
                limbs.get(3).update();
            } else if(state == 2){
                limbs.get(1).update();
                limbs.get(2).update();
            }
        }

    }
    int w = 1200;
    int h = 1200;

    Scene scene;
    float boneLength = 50;
    Skeleton skeleton;
    IKController controller;
    public void settings() {
        size(w, h, P3D);
    }

    public void setup() {
        Joint.depth = true;
        //Setting the scene
        scene = new Scene(this);
        scene.setRightHanded();
        scene.setRadius(boneLength * 10);
        scene.fit(1);
        //sk
        skeleton = generateSkeleton(scene);
        controller = new IKController(skeleton);
    }

    public void draw() {
        background(0);
        scene.drawAxes();
        scene.render();
        move();
    }

    public void mouseMoved() {
        scene.mouseTag();
    }

    public void mouseDragged() {
        if (mouseButton == LEFT){
            scene.mouseSpin();
        } else if (mouseButton == RIGHT) {
            scene.mouseTranslate();
        } else {
            scene.scale(mouseX - pmouseX);
        }
    }

    public void mouseWheel(MouseEvent event) {
        scene.scale(event.getCount() * 20);
    }

    public void mouseClicked(MouseEvent event) {
        if (event.getCount() == 2)
            if (event.getButton() == LEFT)
                scene.focus();
            else
                scene.align();
    }

    public void fixLength(Vector v){
        v.normalize();
        v.multiply(boneLength);
    }

    //crear esqueleto
    Skeleton generateSkeleton(Scene scene){
        Vector x = new Vector(1,0,0);
        fixLength(x);
        Skeleton sk = new Skeleton(scene);
        Node n1 = sk.addJoint("Hips");
        n1.translate(x,0);
        n1.setConstraint(new Constraint() {
            @Override
            public Vector constrainTranslation(Vector translation, Node node) {
                return translation;
            }

            @Override
            public Quaternion constrainRotation(Quaternion rotation, Node node) {
                return new Quaternion();
            }
        });

        Node n2 = sk.addJoint("Chest", "Hips");
        n2.translate(Vector.multiply(x, 2),0);

        n2.setConstraint(new Constraint() {
            @Override
            public Vector constrainTranslation(Vector translation, Node node) {
                return translation;
            }

            @Override
            public Quaternion constrainRotation(Quaternion rotation, Node node) {
                return new Quaternion();
            }
        });

        Node n3 = sk.addJoint("Neck", "Chest");
        n3.translate(x,0);
        Node n4 = generateHead(scene.radius() * 0.2f);
        sk.addJoint("Head", "Neck", n4);
        Vector t = new Vector(1,1,0);
        fixLength(t);
        t.multiply(2);
        n4.translate(t, 0);
        //Constraint neck
        Hinge neckConstraint = new Hinge(radians(45),radians(45));
        neckConstraint.setRestRotation(n3.rotation().get(), new Vector(1,0,0), new Vector(0,1,0));
        n3.setConstraint(neckConstraint);

        sk.updateConstraints();

        //limbs
        generateLimb(sk, "Hips", true, "Back Limb Left");
        generateLimb(sk, "Hips", false, "Back Limb Right");
        generateLimb(sk, "Chest", true, "Front Limb Left");
        generateLimb(sk, "Chest", false, "Front Limb Right");

        //Add ik
        sk.enableIK();
        //Add targets of limbs
        sk.addTarget("Front Limb Left 3").setReference(null);
        sk.addTarget("Front Limb Right 3").setReference(null);
        sk.addTarget("Back Limb Left 3").setReference(null);
        sk.addTarget("Back Limb Right 3").setReference(null);

        return sk;
    }


    void generateLimb(Skeleton skeleton, String parent, boolean isLeft, String name){
        Node n1 = skeleton.addJoint(name + " 1", parent);
        Node n2 = skeleton.addJoint(name + " 2", name + " 1");
        Node n2_hat = skeleton.addJoint(name + " 2 hat", name + " 2");
        Node n3 = skeleton.addJoint(name + " 3", name + " 2 hat");
        //set position
        Vector t1 , t2;
        if(isLeft){
            t1 = new Vector(0, -1, -1);
            t2 = new Vector(0, -1, -0.5f);
        } else{
            t1 = new Vector(0, -1, 1);
            t2 = new Vector(0, -1, 0.5f);
        }
        fixLength(t1);
        fixLength(t2);
        n1.translate(t1);
        n2.translate(t2);
        n2_hat.translate(t2);
        n3.translate(t2);
    }

    Node generateHead(final float r){
        Node n1 = new Node(){
            @Override
            public void graphics(PGraphics pg){
                pg.pushStyle();
                pg.fill(255,0,0);
                pg.stroke(255,0,0);
                pg.strokeWeight(((Joint)reference()).radius() * 0.3f);
                Vector p = location(reference());
                float mag = p.magnitude() - ((Joint)reference()).radius();
                p.normalize();
                p.multiply(mag);
                pg.line(0,0,0,p.x(), p.y(),p.z());
                pg.noStroke();
                pg.sphere(r);
                //eyes
                float eye_r = r * 0.25f;
                float x_offset = r * 0.75f;
                float z_offset = sqrt(r * r - x_offset * x_offset);
                pg.fill(0);
                pushMatrix();
                pg.translate(x_offset,0, z_offset);
                pg.sphere(eye_r);
                popMatrix();
                pushMatrix();
                pg.translate(x_offset,0, -z_offset);
                pg.sphere(eye_r);
                popMatrix();

                pg.popStyle();
            }
        };
        return n1;
    }


    //Mover con las teclas
    boolean keyLeft, keyRight, keyUp, keyDown, keyYUp, keyYDown, rotPos, rotNeg;
    void move(){
        float x = 0, y = 0, z = 0;
        float angle = 0;
        if(keyLeft) z -= 1;
        if(keyRight) z += 1;
        if(keyUp) x += 1;
        if(keyDown) x -= 1;
        if(rotPos) angle += 1;
        if(rotNeg) angle -= 1;
        if(keyYUp) y += 1;
        if(keyYDown) y -= 1;

        controller.move(new Vector(x,y,z));
        if(abs(angle) > 0) controller.turn(angle);
    }

    void enableMovement(boolean value){
        if (keyCode == 'W' || keyCode == 'w') {
            keyUp = value;
        }
        if (keyCode == 'S' || keyCode == 's') {
            keyDown = value;
        }
        if (keyCode == 'A' || keyCode == 'a') {
            rotPos = value;
        }
        if (keyCode == 'D' || keyCode == 'd') {
            rotNeg = value;
        }
        if (keyCode == 'Q' || keyCode == 'q') {
            keyLeft = value;
        }
        if (keyCode == 'E' || keyCode == 'e') {
            keyRight = value;
        }
        if (keyCode == UP) {
            keyYUp = value;
        }
        if (keyCode == DOWN) {
            keyYDown = value;
        }
    }

    public void keyPressed() {
        enableMovement(true);
    }

    public void keyReleased() {
        enableMovement(false);
    }

    static public void main(String args[]) {
        PApplet.main(new String[] { "Main" });
    }
}
