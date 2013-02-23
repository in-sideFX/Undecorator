package insidefx.undecorator;

import java.util.logging.Level;
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

/**
 *
 * @author in-sideFX
 */
public class UndecoratorController {

    private static double initX = -1;
    private static double initY = -1;
    private static double newX;
    private static double newY;
    private static int RESIZE_PADDING;
    private static int SHADOW_WIDTH;
    Undecorator undecorator;
    BoundingBox savedBounds;
    static boolean isMacOS = false;

    {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("mac") != -1) {
            isMacOS = true;
        }
    }

    public UndecoratorController(Undecorator ud) {
        undecorator = ud;
    }

    /*
     * Actions
     */
    protected void maximizeOrRestore() {
        Stage stage = undecorator.getStage();

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

            stage.setX(visualBounds.getMinX());
            stage.setY(visualBounds.getMinY());
            stage.setWidth(visualBounds.getWidth());
            stage.setHeight(visualBounds.getHeight());

        }
    }

    public void close() {
        Stage stage = undecorator.getStage();
        stage.hide();   // TODO: real close
    }

    public void minimize() {
        Stage stage = undecorator.getStage();
        stage.setIconified(true);
    }

    /**
     * Stage resize management
     *
     * @param stage
     * @param node
     * @param PADDING
     * @param SHADOW
     */
    public void setStageResizableWith(final Stage stage, final Node node, int PADDING, int SHADOW) {

        RESIZE_PADDING = PADDING;
        SHADOW_WIDTH = SHADOW;
        node.setOnMouseClicked(new EventHandler<MouseEvent>() {
            // Maximize on double click
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() > 1)// && Cursor.N_RESIZE.equals(node.getCursor()))
                {
                    undecorator.maximizeProperty().set(!undecorator.maximizeProperty().get());
                    mouseEvent.consume();
                }
            }
        });

        node.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.isPrimaryButtonDown()) {
                    initX = mouseEvent.getScreenX();
                    initY = mouseEvent.getScreenY();
                    mouseEvent.consume();
                }
            }
        });
        node.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!mouseEvent.isPrimaryButtonDown() || (initX == -1 && initY == -1)) {
                    return;
                }
                if (savedBounds != null) {
                    return; // maximized mode does not support drag
                }
                newX = mouseEvent.getScreenX();
                newY = mouseEvent.getScreenY();
                double deltax = newX - initX;
                double deltay = newY - initY;

                Cursor cursor = node.getCursor();
                if (Cursor.E_RESIZE.equals(cursor)) {
                    setStageWidth(stage, stage.getWidth() + deltax);
                    mouseEvent.consume();
                } else if (Cursor.NE_RESIZE.equals(cursor)) {
                    if (setStageHeight(stage, stage.getHeight() - deltay)) {
                        setStageY(stage, stage.getY() + deltay);
                    }
                    setStageWidth(stage, stage.getWidth() + deltax);
                    mouseEvent.consume();
                } else if (Cursor.SE_RESIZE.equals(cursor)) {
                    setStageWidth(stage, stage.getWidth() + deltax);
                    setStageHeight(stage, stage.getHeight() + deltay);
                    mouseEvent.consume();
                } else if (Cursor.S_RESIZE.equals(cursor)) {
                    setStageHeight(stage, stage.getHeight() + deltay);
                    mouseEvent.consume();
                } else if (Cursor.W_RESIZE.equals(cursor)) {
                    if (setStageWidth(stage, stage.getWidth() - deltax)) {
                        stage.setX(stage.getX() + deltax);
                    }
                    mouseEvent.consume();
                } else if (Cursor.SW_RESIZE.equals(cursor)) {
                    if (setStageWidth(stage, stage.getWidth() - deltax)) {
                        stage.setX(stage.getX() + deltax);
                    }
                    setStageHeight(stage, stage.getHeight() + deltay);
                    mouseEvent.consume();
                } else if (Cursor.NW_RESIZE.equals(cursor)) {
                    if (setStageWidth(stage, stage.getWidth() - deltax)) {
                        stage.setX(stage.getX() + deltax);
                    }
                    if (setStageHeight(stage, stage.getHeight() - deltay)) {
                        setStageY(stage, stage.getY() + deltay);
                    }
                    mouseEvent.consume();
                } else if (Cursor.N_RESIZE.equals(cursor)) {
                    if (setStageHeight(stage, stage.getHeight() - deltay)) {
                        setStageY(stage, stage.getY() + deltay);
                    }
                    mouseEvent.consume();
                }

            }
        });
        node.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (savedBounds != null) {
                    setCursor(node, Cursor.DEFAULT);
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

    /**
     * Under Windows, the undecorator Stage could be been dragged below the Task
     * bar and then no way to grab it again... TODO: Optimize...
     *
     * @param y
     */
    void setStageY(Stage stage, double y) {
        try {
            ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
            if (screensForRectangle.size() > 0) {
                Screen screen = screensForRectangle.get(0);
                Rectangle2D visualBounds = screen.getVisualBounds();
                if (y < visualBounds.getHeight() - 30) {
                    stage.setY(y);
                }
            }
        } catch (Exception e) {
            Undecorator.LOGGER.log(Level.SEVERE, "setStageY issue", e);
        }
    }

    boolean setStageWidth(Stage stage, double width) {
        if (width >= stage.getMinWidth()) {
            stage.setWidth(width);
            initX = newX;
            return true;
        }
        return false;
    }

    boolean setStageHeight(Stage stage, double height) {
        if (height >= stage.getMinHeight()) {
            stage.setHeight(height);
            initY = newY;
            return true;
        }
        return false;
    }

    /**
     * Allow this node to drag the Stage
     *
     * @param stage
     * @param node
     */
    public void setAsStageDraggable(final Stage stage, final Node node) {

        node.setOnMouseClicked(new EventHandler<MouseEvent>() {
            // Maximize on double click
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() > 1) {
                    undecorator.maximizeProperty().set(!undecorator.maximizeProperty().get());
                    mouseEvent.consume();
                }
            }
        });
        node.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.isPrimaryButtonDown()) {
                    initX = mouseEvent.getScreenX();
                    initY = mouseEvent.getScreenY();
                    mouseEvent.consume();
                } else {
                    initX = -1;
                    initY = -1;
                }
            }
        });
        node.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setCursor(node, Cursor.DEFAULT);
                initX = -1;
                initY = -1;
            }
        });
        node.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!mouseEvent.isPrimaryButtonDown() || initX == -1) {
                    return;
                }
                if (savedBounds != null) {
                    return; // maximized mode does not support drag
                }
                double newX = mouseEvent.getScreenX();
                double newY = mouseEvent.getScreenY();
                double deltax = newX - initX;
                double deltay = newY - initY;
                initX = newX;
                initY = newY;
                setCursor(node, Cursor.HAND);
                stage.setX(stage.getX() + deltax);
                setStageY(stage, stage.getY() + deltay);

                testEdges(stage, mouseEvent);
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

    /**
     * Simulate Windows behavior on screen's edges
     */
    void testEdges(Stage stage, MouseEvent mouseEvent) {
        /*
        ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
        Screen screen = screensForRectangle.get(0);
        Rectangle2D visualBounds = screen.getVisualBounds();
        if (mouseEvent.getScreenX() == visualBounds.getMinX()) {
            System.err.println("Dock Left");
        } else if (mouseEvent.getScreenX() >= visualBounds.getMaxX() - 1) { // MaxX returns the width? Not width -1 ?!
            System.err.println("Dock Right");
        } else if (mouseEvent.getScreenY() == visualBounds.getMinY()) {
            undecorator.maximizeProperty.set(true);
        }
*/

    }

    public boolean isRightEdge(double x, double y, Bounds boundsInParent) {
        if (x < boundsInParent.getWidth() && x > boundsInParent.getWidth() - RESIZE_PADDING - SHADOW_WIDTH) {
            return true;
        }
        return false;
    }

    public boolean isTopEdge(double x, double y, Bounds boundsInParent) {
        if (y >= 0 && y < RESIZE_PADDING + SHADOW_WIDTH) {
            return true;
        }
        return false;
    }

    public boolean isBottomEdge(double x, double y, Bounds boundsInParent) {
        if (y < boundsInParent.getHeight() && y > boundsInParent.getHeight() - RESIZE_PADDING - SHADOW_WIDTH) {
            return true;
        }
        return false;
    }

    public boolean isLeftEdge(double x, double y, Bounds boundsInParent) {
        if (x >= 0 && x < RESIZE_PADDING + SHADOW_WIDTH) {
            return true;
        }
        return false;
    }

    public void setCursor(Node n, Cursor c) {
        n.setCursor(c);
    }
}
