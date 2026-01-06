// ... existing code ...
package ensisa.curves.commands;

import ensisa.curves.MainController;
import javafx.scene.image.Image;

public class LoadImageCommand implements UndoableCommand {
    private final MainController controller;
    private final Image oldImage;
    private final Image newImage;

    public LoadImageCommand(MainController controller, Image oldImage, Image newImage) {
        this.controller = controller;
        this.oldImage = oldImage;
        this.newImage = newImage;
    }

    @Override
    public void execute() {
        controller.setOriginalImage(newImage);
    }

    @Override
    public void undo() {
        controller.setOriginalImage(oldImage);
    }
}
// ... existing code ...