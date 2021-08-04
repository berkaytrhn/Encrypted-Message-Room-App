import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class UserNameAlertBox{

    Stage window;
    Client client;

    public UserNameAlertBox(Client object) {
        this.client = object;
    }

    public void display() throws Exception {
        this.window = new Stage();
        this.window.setTitle("Input");

        //major layout
        VBox mainLayout = new VBox();

        //minor layout
        HBox inputField = new HBox();
        inputField.setSpacing(30);
        inputField.setPadding(new Insets(5,0,0,0));

        //inner layout
        VBox secondInputField = new VBox();
        secondInputField.setSpacing(5);

        //text
        Label requestText = new Label();
        requestText.setText("Enter user name:");

        //input
        Client.userNameInput = new TextField();
        Client.userNameInput.setPrefWidth(220);

        //icon
        Image question = new Image("question.png");
        ImageView questionTemp = new ImageView(question);
        StackPane questionImage = new StackPane();
        questionImage.getChildren().addAll(questionTemp);
        questionImage.setPadding(new Insets(0,-15,-5,15));

        secondInputField.getChildren().addAll(requestText, Client.userNameInput);
        inputField.getChildren().addAll(questionImage,secondInputField);

        //button part
        HBox eventButtons = new HBox();
        eventButtons.setSpacing(5);
        eventButtons.setPadding(new Insets(7,0,0,120));

        //creating buttons
        Client.okay = new Button("OK");
        Client.cancel = new Button("Cancel");
        Client.okay.setOnAction(this.client);
        Client.cancel.setOnAction(this.client);

        //sum the buttons
        eventButtons.getChildren().addAll(Client.okay, Client.cancel);

        //final layout
        mainLayout.getChildren().addAll(inputField,eventButtons);

        window.initStyle(StageStyle.UTILITY);
        Scene scene = new Scene(mainLayout,300,100);
        window.setScene(scene);
        window.setAlwaysOnTop(true);
        window.initModality(Modality.APPLICATION_MODAL);
        window.show();
    }

    public void closeWindow(){
        this.window.close();
    }

}
