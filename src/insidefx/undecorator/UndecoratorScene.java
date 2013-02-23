/*
 * Copyright (c) 2013, Arnaud Nouard
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the In-SideFX nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package insidefx.undecorator;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author In-SideFX
 */
public class UndecoratorScene extends Scene {

    static public final String DEFAULT_STYLESHEET = "skin/undecorator.css";
    static public final String DEFAULT_STAGEDECORATION = "stagedecoration.fxml";
    Undecorator undecorator;

    public UndecoratorScene(Stage stage, Parent root) {
        this(stage, root, DEFAULT_STAGEDECORATION);
    }

    public UndecoratorScene(Stage stage, Parent root, String stageDecorationFxml) {

        super(root);

        undecorator = new Undecorator(stage, root, stageDecorationFxml);
        super.setRoot(undecorator);

        // Customize it by CSS if needed:
        undecorator.getStylesheets().add(DEFAULT_STYLESHEET);

        // Transparent scene and stage
        stage.initStyle(StageStyle.TRANSPARENT);
        super.setFill(Color.TRANSPARENT);
    }

    public void removeDefaultStylesheet() {
        undecorator.getStylesheets().remove(DEFAULT_STYLESHEET);
    }

    public void addStylesheet(String css) {
        undecorator.getStylesheets().add(css);
    }

    public void setAsStageDraggable(Stage stage, Node node) {
        undecorator.setAsStageDraggable(stage, node);
    }
}
