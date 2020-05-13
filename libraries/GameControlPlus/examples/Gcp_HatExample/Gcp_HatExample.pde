/**
 Demonstrates how to get the input from a 'hat' control.
 
 The hat switch is a simple digital joystick with 4 switches,
 up, down, left and right. This sketch demonstrates how you 
 might get the hat inputs. You will need to create your own
 configuration file.
 
 for Processing V3
 (c) 2020 Peter Lager
 */
 
import net.java.games.input.*;
import org.gamecontrolplus.*;
import org.gamecontrolplus.gui.*;

ControlIO control;
Configuration config;
ControlDevice device;
ControlHat hat;

// Variables to hold hat inputs
boolean hat_left, hat_right, hat_up, hat_down;
float hat_x, hat_y;

// Variables for hat drawing only
float hx = 90, hy = 90;
float hrad = 70, hbrad = 0.7*hrad, hbsize= 0.15 *hrad;

void setup() {
  size(200, 280);
  surface.setTitle("GCP Cooliehat example");
  // Initialise the ControlIO
  control = ControlIO.getInstance(this);
  // Find a device that matches the configuration file
  device = control.getMatchedDevice("hat-config");
  if (device == null) {
    println("No suitable device configured");
    System.exit(-1); // End the program NOW!
  }
  hat = device.getHat("HAT");
  textSize(16);
}

void draw() {
  getUserInput();
  background(220);
  // ===========================================
  // Hat face
  stroke(40, 40, 200);
  strokeWeight(1.5);
  fill(200, 200, 250);
  ellipse(hx, hy, 2*hrad, 2*hrad);
  // Hat direction buttons
  stroke(0);
  strokeWeight(0.9);
  fill(button_color(hat_left));
  ellipse(hx - hbrad, hy, hbsize * 2, hbsize * 2);
  fill(button_color(hat_right));
  ellipse(hx + hbrad, hy, hbsize * 2, hbsize * 2);
  fill(button_color(hat_up));
  ellipse(hx, hy - hbrad, hbsize * 2, hbsize * 2);
  fill(button_color(hat_down));
  ellipse(hx, hy + hbrad, hbsize * 2, hbsize * 2);
  // Hat direction values
  fill(0);
  text("X: " + hat_x, 20, 200, 200, 20);
  text("Y: " + hat_y, 20, 230, 200, 20);
}

int button_color(boolean pressed) {
  return pressed ? 0xffee2222 : 0xff771111;
}

// Poll for user input called from the draw() method.
public void getUserInput() {
  hat_left = hat.left();
  hat_right = hat.right();
  hat_up = hat.up();
  hat_down = hat.down();
  hat_x = hat.getX();
  hat_y = hat.getY();
}
