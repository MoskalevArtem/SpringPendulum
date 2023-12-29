module main.demo {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens main.demo to javafx.fxml;
    exports main.demo;
}