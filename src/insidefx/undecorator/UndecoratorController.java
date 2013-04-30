package insidefx.undecorator;

import java.util.logging.Level;
import javafx.application.Platform;
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
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 *
 * @author in-sideFX
 */
public class UndecoratorController {

    static final int DOCK_NONE = 0x0;
    static final int DOCK_LEFT = 0x1;
    static final int DOCK_RIGHT = 0x2;
    static final int DOCK_TOP = 0x4;
    int lastDocked = DOCK_NONE;
    private static double initX = -1;
    private static double initY = -1;
    private static double newX;
    private static double newY;
    private static int RESIZE_PADDING;
    private static int SHADOW_WIDTH;
    Undecorator undecorator;
    BoundingBox savedBounds, savedFullScreenBounds;
    boolean maximized = false;
    static boolean isMacOS = false;
    static final int MAXIMIZE_BORDER = 20;  // Allow double click to maximize on top of the Scene

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

        if (maximized) {
            restoreSavedBounds(stage, false);
            undecorator.setShadow(true);
            savedBounds = null;
            maximized = false;
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
            maximized = true;
        }
    }

    public void saveBounds() {
        Stage stage = undecorator.getStage();
        savedBounds = new BoundingBox(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
    }

    public void saveFullScreenBounds() {
        Stage stage = undecorator.getStage();
        savedFullScreenBounds = new BoundingBox(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
    }

    public void restoreSavedBounds(Stage stage, boolean fullscreen) {

        stage.setX(savedBounds.getMinX());
        stage.setY(savedBounds.getMinY());
        stage.setWidth(savedBounds.getWidth());
        stage.setHeight(savedBounds.getHeight());
        savedBounds = null;
    }

    public void restoreFullScreenSavedBounds(Stage stage) {

        stage.setX(savedFullScreenBounds.getMinX());
        stage.setY(savedFullScreenBounds.getMinY());
        stage.setWidth(savedFullScreenBounds.getWidth());
        stage.setHeight(savedFullScreenBounds.getHeight());
        savedFullScreenBounds = null;
    }
    /**
     * Sets the stage to the center of the screen.
     */
    public void setCenterStage(Stage stage){
	Screen screen = Screen.getPrimary();
	Rectangle2D bounds = screen.getVisualBounds();

	double width = stage.getMinWidth();
	double height = stage.getMinHeight();

	// calculate x
	double x = bounds.getWidth()/2 - width/2;
	double y = bounds.getHeight()/2 - height/2;
	savedBounds = new BoundingBox(x, y, stage.getWidth(), stage.getHeight());
        stage.setX(x);
        stage.setY(y);
    }

    protected void setFullScreen(boolean value) {
        Stage stage = undecorator.getStage();
        stage.setFullScreen(value);
    }

    public void close() {
        final Stage stage = undecorator.getStage();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        });
        
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
                if (undecorator.getStageStyle() != StageStyle.UTILITY && !stage.isFullScreen() && mouseEvent.getClickCount() > 1) {
                    if (mouseEvent.getSceneY() - SHADOW_WIDTH < MAXIMIZE_BORDER) {
			undecorator.toggleMaximizeProperty();
                        mouseEvent.consume();
                    }
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
                if (stage.isFullScreen()) {
                    return;
                }
                /*
                 * Long press generates drag event!
                 */
                if (mouseEvent.isStillSincePress()) {
                    return;
                }
                if (maximized) {
                    // Remove maximized state
                    undecorator.maximizeProperty.set(false);
                    return;
                } // Docked then moved, so restore state
                else if (savedBounds != null) {
                    undecorator.setShadow(true);
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
                if (maximized) {
                    setCursor(node, Cursor.DEFAULT);
                    return; // maximized mode does not support resize
                }
                if (stage.isFullScreen()) {
                    return;
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
     * bar and then no way to grab it again...
     * On Mac, do not drag under the menu bar
     *
     * @param y
     */
    public void setStageY(Stage stage, double y) {
        try {
            ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
            if (screensForRectangle.size() > 0) {
                Screen screen = screensForRectangle.get(0);
                Rectangle2D visualBounds = screen.getVisualBounds();
                if (y < visualBounds.getHeight() - 30 && y+SHADOW_WIDTH >= visualBounds.getMinY()) {
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
                if (undecorator.getStageStyle() != StageStyle.UTILITY && !stage.isFullScreen() && mouseEvent.getClickCount() > 1) {
                    if (mouseEvent.getSceneY() - SHADOW_WIDTH < MAXIMIZE_BORDER) {
			undecorator.toggleMaximizeProperty();
                        mouseEvent.consume();
                    }
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
        node.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!mouseEvent.isPrimaryButtonDown() || initX == -1) {
                    return;
                }
                if (stage.isFullScreen()) {
                    return;
                }
                /*
                 * Long press generates drag event!
                 */
                if (mouseEvent.isStillSincePress()) {
                    return;
                }
                if (maximized) {
                    // Remove Maximized state
                    undecorator.maximizeProperty.set(false);
                    // Center 
                    stage.setX(mouseEvent.getScreenX() - stage.getWidth() / 2);
                    stage.setY(mouseEvent.getScreenY() - SHADOW_WIDTH);
                } // Docked then moved, so restore state
                else if (savedBounds != null) {
                    restoreSavedBounds(stage, false);
                    undecorator.setShadow(true);
                    // Center
                    stage.setX(mouseEvent.getScreenX() - stage.getWidth() / 2);
                    stage.setY(mouseEvent.getScreenY() - SHADOW_WIDTH);
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

                testDock(stage, mouseEvent);
                mouseEvent.consume();
            }
        });
        node.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                undecorator.setDockFeedbackUnVisible();
                setCursor(node, Cursor.DEFAULT);
                initX = -1;
                initY = -1;
                dockActions(stage, t);
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
     * (Humble) Simulation of Windows behavior on screen's edges Feedbacks
     */
    void testDock(Stage stage, MouseEvent mouseEvent) {


        int dockSide = getDockSide(mouseEvent);
        // Dock Left
        if (dockSide == DOCK_LEFT) {
            if (lastDocked == DOCK_LEFT) {
                return;
            }
            double x = mouseEvent.getSceneX() - SHADOW_WIDTH;
            double y = mouseEvent.getSceneY() - Undecorator.FEEDBACK_SIZE;
            undecorator.setDockFeedbackVisible(x, y, Undecorator.FEEDBACK_SIZE, Undecorator.FEEDBACK_SIZE * 2);
            lastDocked = DOCK_LEFT;
        } // Dock Right
        else if (dockSide == DOCK_RIGHT) {
            if (lastDocked == DOCK_RIGHT) {
                return;
            }
            double x = mouseEvent.getSceneX() - Undecorator.FEEDBACK_SIZE - Undecorator.FEEDBACK_STROKE * 2 - SHADOW_WIDTH;
            double y = mouseEvent.getSceneY() - Undecorator.FEEDBACK_SIZE;
            undecorator.setDockFeedbackVisible(x, y, Undecorator.FEEDBACK_SIZE, Undecorator.FEEDBACK_SIZE * 2);
            lastDocked = DOCK_RIGHT;
        } // Dock top
        else if (dockSide == DOCK_TOP) {
            if (lastDocked == DOCK_TOP) {
                return;
            }
            double x = mouseEvent.getSceneX() - Undecorator.FEEDBACK_SIZE - SHADOW_WIDTH;
            double y = mouseEvent.getSceneY() - SHADOW_WIDTH;
            undecorator.setDockFeedbackVisible(x, y, Undecorator.FEEDBACK_SIZE * 2, Undecorator.FEEDBACK_SIZE);
            lastDocked = DOCK_TOP;
        } else {
            lastDocked = DOCK_NONE;
        }
    }

    /**
     * Based on mouse position returns dock side
     *
     * @param mouseEvent
     * @return DOCK_LEFT,DOCK_RIGHT,DOCK_TOP
     */
    int getDockSide(MouseEvent mouseEvent) {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = 0;
        double maxY = 0;

        // Get "big" screen bounds
        ObservableList<Screen> screens = Screen.getScreens();
        for (Screen screen : screens) {
            Rectangle2D visualBounds = screen.getVisualBounds();
            minX = Math.min(minX, visualBounds.getMinX());
            minY = Math.min(minY, visualBounds.getMinY());
            maxX = Math.max(maxX, visualBounds.getMaxX());
            maxY = Math.max(maxY, visualBounds.getMaxY());
        }
        // Dock Left
        if (mouseEvent.getScreenX() == minX) {
            return DOCK_LEFT;
        } else if (mouseEvent.getScreenX() >= maxX - 1) { // MaxX returns the width? Not width -1 ?!
            return DOCK_RIGHT;
        } else if (mouseEvent.getScreenY() <= minY) {   // Mac menu bar
            return DOCK_TOP;
        }
        return 0;
    }

    /**
     * (Humble) Simulation of Windows behavior on screen's edges Actions
     */
    void dockActions(Stage stage, MouseEvent mouseEvent) {
        ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
        Screen screen = screensForRectangle.get(0);
        Rectangle2D visualBounds = screen.getVisualBounds();
        // Dock Left
        if (mouseEvent.getScreenX() == visualBounds.getMinX()) {
            savedBounds = new BoundingBox(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());

//            stage.setX(visualBounds.getMinX() - SHADOW_WIDTH);
//            stage.setY(visualBounds.getMinY() - SHADOW_WIDTH);
//            stage.setWidth(visualBounds.getWidth() / 2);
//            stage.setHeight(visualBounds.getHeight() + 2 * SHADOW_WIDTH);
            stage.setX(visualBounds.getMinX());
            stage.setY(visualBounds.getMinY());
            stage.setWidth(visualBounds.getWidth() / 2);
            stage.setHeight(visualBounds.getHeight());
            undecorator.setShadow(false);
        } // Dock Right
        else if (mouseEvent.getScreenX() >= visualBounds.getMaxX() - 1) { // MaxX returns the width? Not width -1 ?!
            savedBounds = new BoundingBox(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());

//            stage.setX(visualBounds.getWidth() / 2);
//            stage.setY(visualBounds.getMinY() - SHADOW_WIDTH);
//            stage.setWidth(visualBounds.getWidth() / 2 + SHADOW_WIDTH);
//            stage.setHeight(visualBounds.getHeight() + 2 * SHADOW_WIDTH);
            stage.setX(visualBounds.getWidth() / 2);
            stage.setY(visualBounds.getMinY());
            stage.setWidth(visualBounds.getWidth() / 2);
            stage.setHeight(visualBounds.getHeight());
            undecorator.setShadow(false);
        } else if (mouseEvent.getScreenY() <= visualBounds.getMinY()) { // Mac menu bar
            undecorator.maximizeProperty.set(true);
        }

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
