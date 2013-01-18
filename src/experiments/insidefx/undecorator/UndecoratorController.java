/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package experiments.insideFX.undecorator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Window;

/**
 *
 * @author in-sideFX TODO: Manage minimum size Multiple screen Maximization
 * Inject right click icon close... API
 */
public class UndecoratorController {

    private static double initX;
    private static double initY;
    Scene scene;
    private static int RESIZE_PADDING;
    private static int SHADOW_WIDTH;
    private static double dragOffsetX, dragOffsetY;

    public UndecoratorController() {
    }

    public static void setAsResizable(final Window stage, final Node node, int PADDING, int SHADOW) {
        RESIZE_PADDING = PADDING;
        SHADOW_WIDTH=SHADOW;
        node.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                dragOffsetX = (stage.getX() + stage.getWidth()) - mouseEvent.getScreenX();
                dragOffsetY = (stage.getY() + stage.getHeight()) - mouseEvent.getScreenY();

                initX = mouseEvent.getScreenX();
                initY = mouseEvent.getScreenY();
                mouseEvent.consume();
            }
        });
        node.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
//                ObservableList<Screen> screens = Screen.getScreensForRectangle(stage.getX(), stage.getY(), 1, 1);
//                final Screen screen;
//                if (screens.size() > 0) {
//                    screen = Screen.getScreensForRectangle(stage.getX(), stage.getY(), 1, 1).get(0);
//                } else {
//                    screen = Screen.getScreensForRectangle(0, 0, 1, 1).get(0);
//                }
//                Rectangle2D visualBounds = screen.getVisualBounds();
//                double maxX = Math.min(visualBounds.getMaxX(), mouseEvent.getScreenX() + dragOffsetX);
//                double maxY = Math.min(visualBounds.getMaxY(), mouseEvent.getScreenY() - dragOffsetY);
//                stage.setWidth(Math.max(1200, maxX - stage.getX()));
//                stage.setHeight(Math.max(800, maxY - stage.getY()));
                double newX = mouseEvent.getScreenX();
                double newY = mouseEvent.getScreenY();
                double deltax = newX - initX;
                double deltay = newY - initY;
                initX = newX;
                initY = newY;
                Cursor cursor = node.getCursor();
                if (Cursor.E_RESIZE.equals(cursor)) {
                    stage.setWidth(stage.getWidth() + deltax);
                } else if (Cursor.S_RESIZE.equals(cursor)) {
                    stage.setHeight(stage.getHeight() + deltay);
                } else if (Cursor.W_RESIZE.equals(cursor)) {
                    stage.setX(stage.getX() + deltax);
                    stage.setWidth(stage.getWidth() - deltax);
                } else if (Cursor.N_RESIZE.equals(cursor)) {
                    stage.setY(stage.getY() + deltay);
                    stage.setHeight(stage.getHeight() - deltay);
                } else {
                    setCursor(node, Cursor.HAND);
                    stage.setX(stage.getX() + deltax);
                    stage.setY(stage.getY() + deltay);
                }
                mouseEvent.consume();
            }
        });
        node.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                double x = mouseEvent.getX();
                double y = mouseEvent.getY();
                Bounds boundsInParent = node.getBoundsInParent();
                if (isRightEdge(x, y, boundsInParent)) {
                    setCursor(node, Cursor.E_RESIZE);
                } else if (isLeftEdge(x, y, boundsInParent)) {
                    setCursor(node, Cursor.W_RESIZE);
                } else if (isTopEdge(x, y, boundsInParent)) {
                    setCursor(node, Cursor.N_RESIZE);
                } else if (isBottomEdge(x, y, boundsInParent)) {
                    setCursor(node, Cursor.S_RESIZE);
                } else {
                    setCursor(node, Cursor.DEFAULT);
                    System.err.println("DEFAULT");
                }
            }
        });
    }

    public static void setAsDraggable(final Window stage, final Node node) {

        node.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                dragOffsetX = (stage.getX() + stage.getWidth()) - mouseEvent.getScreenX();
                dragOffsetY = (stage.getY() + stage.getHeight()) - mouseEvent.getScreenY();

                initX = mouseEvent.getScreenX();
                initY = mouseEvent.getScreenY();
                mouseEvent.consume();
            }
        });
//        node.onMousePressedProperty().addListener(new ChangeListener<EventHandler<? super MouseEvent>>() {
//            @Override
//            public void changed(ObservableValue<? extends EventHandler<? super MouseEvent>> ov, EventHandler<? super MouseEvent> t, EventHandler<? super MouseEvent> t1) {
//                MouseEvent mouseEvent = (MouseEvent) t1;
//                dragOffsetX = (stage.getX() + stage.getWidth()) - mouseEvent.getScreenX();
//                dragOffsetY = (stage.getY() + stage.getHeight()) - mouseEvent.getScreenY();
//                
//                initX = mouseEvent.getScreenX();
//                initY = mouseEvent.getScreenY();
//                mouseEvent.consume();
//            }
//        });
        node.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setCursor(node, Cursor.DEFAULT);
            }
        });
//        node.onMouseDraggedProperty().addListener(new ChangeListener<EventHandler<? super MouseEvent>>() {
//            @Override
//            public void changed(ObservableValue<? extends EventHandler<? super MouseEvent>> ov, EventHandler<? super MouseEvent> t, EventHandler<? super MouseEvent> t1) {
//               MouseEvent mouseEvent = (MouseEvent) t1;
//                
//                double newX = mouseEvent.getScreenX();
//                double newY = mouseEvent.getScreenY();
//                double deltax = newX - initX;
//                double deltay = newY - initY;
//                initX = newX;
//                initY = newY;
//                Cursor cursor = node.getCursor();
//                if (Cursor.E_RESIZE.equals(cursor)) {
//                    stage.setWidth(stage.getWidth() + deltax);
//                } else if (Cursor.S_RESIZE.equals(cursor)) {
//                    stage.setHeight(stage.getHeight() + deltay);
//                } else if (Cursor.W_RESIZE.equals(cursor)) {
//                    stage.setX(stage.getX() + deltax);
//                    stage.setWidth(stage.getWidth() - deltax);
//                } else if (Cursor.N_RESIZE.equals(cursor)) {
//                    stage.setY(stage.getY() + deltay);
//                    stage.setHeight(stage.getHeight() - deltay);
//                } else {
//                    setCursor(node, Cursor.HAND);
//                    stage.setX(stage.getX() + deltax);
//                    stage.setY(stage.getY() + deltay);
//                }
//                mouseEvent.consume();
//            }
//        });
        node.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
//                ObservableList<Screen> screens = Screen.getScreensForRectangle(stage.getX(), stage.getY(), 1, 1);
//                final Screen screen;
//                if (screens.size() > 0) {
//                    screen = Screen.getScreensForRectangle(stage.getX(), stage.getY(), 1, 1).get(0);
//                } else {
//                    screen = Screen.getScreensForRectangle(0, 0, 1, 1).get(0);
//                }
//                Rectangle2D visualBounds = screen.getVisualBounds();
//                double maxX = Math.min(visualBounds.getMaxX(), mouseEvent.getScreenX() + dragOffsetX);
//                double maxY = Math.min(visualBounds.getMaxY(), mouseEvent.getScreenY() - dragOffsetY);
//                stage.setWidth(Math.max(1200, maxX - stage.getX()));
//                stage.setHeight(Math.max(800, maxY - stage.getY()));
                double newX = mouseEvent.getScreenX();
                double newY = mouseEvent.getScreenY();
                double deltax = newX - initX;
                double deltay = newY - initY;
                initX = newX;
                initY = newY;
                Cursor cursor = node.getCursor();
//                if (Cursor.E_RESIZE.equals(cursor)) {
//                    stage.setWidth(stage.getWidth() + deltax);
//                } else if (Cursor.S_RESIZE.equals(cursor)) {
//                    stage.setHeight(stage.getHeight() + deltay);
//                } else if (Cursor.W_RESIZE.equals(cursor)) {
//                    stage.setX(stage.getX() + deltax);
//                    stage.setWidth(stage.getWidth() - deltax);
//                } else if (Cursor.N_RESIZE.equals(cursor)) {
//                    stage.setY(stage.getY() + deltay);
//                    stage.setHeight(stage.getHeight() - deltay);
//                } else 
                {
                    setCursor(node, Cursor.HAND);
                    stage.setX(stage.getX() + deltax);
                    stage.setY(stage.getY() + deltay);
                }
                mouseEvent.consume();
            }
        });

        node.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            }
        });

        node.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //setCursor(node, Cursor.DEFAULT);
            }
        });
    }

    public static boolean isRightEdge(double x, double y, Bounds boundsInParent) {
        if (x < boundsInParent.getWidth() && x > boundsInParent.getWidth() - RESIZE_PADDING - SHADOW_WIDTH) {
            return true;
        }
        return false;
    }

    public static boolean isTopEdge(double x, double y, Bounds boundsInParent) {
        if (y >= 0 && y < RESIZE_PADDING + SHADOW_WIDTH) {
            return true;
        }
        return false;
    }

    public static boolean isBottomEdge(double x, double y, Bounds boundsInParent) {
        if (y < boundsInParent.getHeight() && y > boundsInParent.getHeight() - RESIZE_PADDING - SHADOW_WIDTH) {
            return true;
        }
        return false;
    }

    public static boolean isLeftEdge(double x, double y, Bounds boundsInParent) {
        if (x >= 0 && x < RESIZE_PADDING + SHADOW_WIDTH) {
            return true;
        }
        return false;
    }

    public static void setCursor(Node n, Cursor c) {
        n.setCursor(c);
    }
//    public static void setAsResizable(final Node node) {
//        node.setOnMousePressed(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent mouseEvent) {
////                initX = stage.getX() - mouseEvent.getScreenX();
////                initY = stage.getY() - mouseEvent.getScreenY();
//
//            }
//        });
//        node.setOnMouseReleased(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent mouseEvent) {
//                setCursor(node,Cursor.DEFAULT);
//            }
//        });
//        node.setOnMouseDragged(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent mouseEvent) {
//                setCursor(node,Cursor.HAND);
////                stage.setX(mouseEvent.getScreenX() + initX);
////                stage.setY(mouseEvent.getScreenY() + initY);
//            }
//        });
//        node.setOnMouseEntered(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent mouseEvent) {
//                setCursor(node, Cursor.MOVE);
//            }
//        });
//        node.setOnMouseMoved(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent mouseEvent) {
//                setCursor(node,Cursor.MOVE);
//            }
//        });
//        node.setOnMouseExited(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent mouseEvent) {
//                setCursor(node,Cursor.DEFAULT);
//            }
//        });
//    }
}
