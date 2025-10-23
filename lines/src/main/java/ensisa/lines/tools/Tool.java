public interface Tool {
    default void mousePressed(MouseEvent event) {}
    default void mouseDragged(MouseEvent event) {}
    default void mouseReleased(MouseEvent event) {}
}