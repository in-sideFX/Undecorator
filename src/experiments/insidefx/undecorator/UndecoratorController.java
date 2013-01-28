package experiments.insidefx.undecorator;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author in-sideFX TODO: Manage minimum size, Multiple screen, Maximization,
 * Inject right click, icons, API + Window Listener, tooltip, no static!, focus
 * on icons
 */
public class UndecoratorController {

    private static double initX;
    private static double initY;
    private static double newX;
    private static double newY;
    private static int RESIZE_PADDING;
    private static int SHADOW_WIDTH;
//    private static double dragOffsetX, dragOffsetY;
    static Undecorator undecorator;
    static BoundingBox savedBounds;

    public UndecoratorController(Undecorator ud) {
        undecorator = ud;
    }

    public static void maximize(Stage stage) {
        if (savedBounds != null) {
            stage.setX(savedBounds.getMinX());
            stage.setY(savedBounds.getMinY());
            stage.setWidth(savedBounds.getWidth());
            stage.setHeight(savedBounds.getHeight());
            undecorator.setShadow(true);
            savedBounds = null;
        } else {
            ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
            Screen screen = screensForRectangle.get(0);
            Rectangle2D visualBounds = screen.getVisualBounds();

            savedBounds = new BoundingBox(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());

            undecorator.setShadow(false);

            stage.setX(0);
            stage.setY(0);
            stage.setWidth(visualBounds.getWidth());
            stage.setHeight(visualBounds.getHeight());
        }
    }

    public static void close(Stage stage) {
        stage.hide();   // TODO: real close
    }

    public static void minimize(Stage stage) {
        stage.setIconified(true);
    }

    public static void setAsResizable(final Stage stage, final Node node, int PADDING, int SHADOW) {
        RESIZE_PADDING = PADDING;
        SHADOW_WIDTH = SHADOW;
        node.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
//                dragOffsetX = (stage.getX() + stage.getWidth()) - mouseEvent.getScreenX();
//                dragOffsetY = (stage.getY() + stage.getHeight()) - mouseEvent.getScreenY();

                initX = mouseEvent.getScreenX();
                initY = mouseEvent.getScreenY();
                mouseEvent.consume();
            }
        });
        node.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (savedBounds != null) {
                    return; // maximized mode does not support drag
                }
                newX = mouseEvent.getScreenX();
                newY = mouseEvent.getScreenY();
                double deltax = newX - initX;
                double deltay = newY - initY;

                Cursor cursor = node.getCursor();
                if (Cursor.E_RESIZE.equals(cursor)) {
                    UndecoratorController.setStageWidth(stage, stage.getWidth() + deltax);
                } else if (Cursor.NE_RESIZE.equals(cursor)) {
                    if (UndecoratorController.setStageHeight(stage, stage.getHeight() - deltay)) {
                        stage.setY(stage.getY() + deltay);
                    }
                    UndecoratorController.setStageWidth(stage, stage.getWidth() + deltax);
                } else if (Cursor.SE_RESIZE.equals(cursor)) {
                    UndecoratorController.setStageWidth(stage, stage.getWidth() + deltax);
                    UndecoratorController.setStageHeight(stage, stage.getHeight() + deltay);
                } else if (Cursor.S_RESIZE.equals(cursor)) {
                    UndecoratorController.setStageHeight(stage, stage.getHeight() + deltay);
                } else if (Cursor.W_RESIZE.equals(cursor)) {
                    if (UndecoratorController.setStageWidth(stage, stage.getWidth() - deltax)) {
                        stage.setX(stage.getX() + deltax);
                    }
                } else if (Cursor.SW_RESIZE.equals(cursor)) {
                    if (UndecoratorController.setStageWidth(stage, stage.getWidth() - deltax)) {
                        stage.setX(stage.getX() + deltax);
                    }
                    UndecoratorController.setStageHeight(stage, stage.getHeight() + deltay);
                } else if (Cursor.NW_RESIZE.equals(cursor)) {
                    if (UndecoratorController.setStageWidth(stage, stage.getWidth() - deltax)) {
                        stage.setX(stage.getX() + deltax);
                    }
                    if (UndecoratorController.setStageHeight(stage, stage.getHeight() - deltay)) {
                        stage.setY(stage.getY() + deltay);
                    }
                } else if (Cursor.N_RESIZE.equals(cursor)) {
                    if (UndecoratorController.setStageHeight(stage, stage.getHeight() - deltay)) {
                        stage.setY(stage.getY() + deltay);
                    }
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
                if (savedBounds != null) {
                    return; // maximized mode does not support drag
                }
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

    static boolean setStageWidth(Stage stage, double width) {
        if (width >= stage.getMinWidth()) {
            stage.setWidth(width);
            initX = newX;
            return true;
        }
        return false;
    }

    static boolean setStageHeight(Stage stage, double height) {
        if (height >= stage.getMinHeight()) {
            stage.setHeight(height);
            initY = newY;
            return true;
        }
        return false;
    }

    public static void setAsDraggable(final Window stage, final Node node) {

        node.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
//                dragOffsetX = (stage.getX() + stage.getWidth()) - mouseEvent.getScreenX();
//                dragOffsetY = (stage.getY() + stage.getHeight()) - mouseEvent.getScreenY();

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
