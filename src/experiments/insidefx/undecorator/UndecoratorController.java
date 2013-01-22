package experiments.insideFX.undecorator;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

/**
 *
 * @author in-sideFX
 * TODO: Manage minimum size, Multiple screen, Maximization,
 * Inject right click, icons, API
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
        SHADOW_WIDTH = SHADOW;
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
                double newX = mouseEvent.getScreenX();
                double newY = mouseEvent.getScreenY();
                double deltax = newX - initX;
                double deltay = newY - initY;
                initX = newX;
                initY = newY;
                Cursor cursor = node.getCursor();
                if (Cursor.E_RESIZE.equals(cursor)) {
                    stage.setWidth(stage.getWidth() + deltax);
                } else if (Cursor.NE_RESIZE.equals(cursor)) {
                    stage.setY(stage.getY() + deltay);
                    stage.setWidth(stage.getWidth() + deltax);
                    stage.setHeight(stage.getHeight() - deltay);
                } else if (Cursor.SE_RESIZE.equals(cursor)) {
                    stage.setWidth(stage.getWidth() + deltax);
                    stage.setHeight(stage.getHeight() + deltay);
                } else if (Cursor.S_RESIZE.equals(cursor)) {
                    stage.setHeight(stage.getHeight() + deltay);
                } else if (Cursor.W_RESIZE.equals(cursor)) {
                    stage.setX(stage.getX() + deltax);
                    stage.setWidth(stage.getWidth() - deltax);
                } else if (Cursor.SW_RESIZE.equals(cursor)) {
                    stage.setX(stage.getX() + deltax);
                    stage.setWidth(stage.getWidth() - deltax);
                    stage.setHeight(stage.getHeight() + deltay);
                } else if (Cursor.NW_RESIZE.equals(cursor)) {
                    stage.setX(stage.getX() + deltax);
                    stage.setY(stage.getY() + deltay);
                    stage.setWidth(stage.getWidth() - deltax);
                    stage.setHeight(stage.getHeight() - deltay);
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
                    if (y < RESIZE_PADDING + SHADOW_WIDTH) {
                        setCursor(node, Cursor.NE_RESIZE);
                    } else if (y > boundsInParent.getHeight() - (double) (RESIZE_PADDING + SHADOW_WIDTH)) {
                        setCursor(node, Cursor.SE_RESIZE);
                    } else {
                        setCursor(node, Cursor.E_RESIZE);
                    }

                } else if (isLeftEdge(x, y, boundsInParent)) {
                    if (y < RESIZE_PADDING + SHADOW_WIDTH) {
                        setCursor(node, Cursor.NW_RESIZE);
                    } else if (y > boundsInParent.getHeight() - (double) (RESIZE_PADDING + SHADOW_WIDTH)) {
                        setCursor(node, Cursor.SW_RESIZE);
                    } else {
                        setCursor(node, Cursor.W_RESIZE);
                    }
                } else if (isTopEdge(x, y, boundsInParent)) {
                    setCursor(node, Cursor.N_RESIZE);
                } else if (isBottomEdge(x, y, boundsInParent)) {
                    setCursor(node, Cursor.S_RESIZE);
                } else {
                    setCursor(node, Cursor.DEFAULT);
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
        node.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setCursor(node, Cursor.DEFAULT);
            }
        });
        node.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                double newX = mouseEvent.getScreenX();
                double newY = mouseEvent.getScreenY();
                double deltax = newX - initX;
                double deltay = newY - initY;
                initX = newX;
                initY = newY;
                setCursor(node, Cursor.HAND);
                stage.setX(stage.getX() + deltax);
                stage.setY(stage.getY() + deltay);
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
}
