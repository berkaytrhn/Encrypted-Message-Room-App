import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;


import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;


public class Client extends Application implements EventHandler<ActionEvent> {

    public static SecretKey AESKey;
    public static SecretKey DESKey;
    public static byte[] initVectorAES;
    public static byte[] initVectorDES;
    public static String method,mode;
    public static Thread clientThread;
    boolean connected;
    Stage window;
    Button encrypt,sendMessage,connect,disconnect;
    TextArea userInput,encryptedText;
    public static RadioButton AES,DES,CBC,OFB;
    Socket socket;
    public static PrintWriter dataOut;
    public static TextArea chatBox;
    public static Button okay,cancel;
    public static TextField userNameInput;
    public static String userName;
    UserNameAlertBox alertBox;
    Label status;

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    public VBox creatingBottomTextFields(String name,boolean editable){
        VBox textField = new VBox();
        textField.setSpacing(5);


        //Creating label
        Label fieldName = new Label();
        fieldName.setText(name);


        //creating text areas
        TextArea input = new TextArea();
        input.setPrefWidth(250);
        input.setPrefHeight(100);
        input.setStyle("-fx-border-color: white");
        if(editable){
            //user input
            input.setEditable(true);
            this.userInput = input;
        }else{
            //crypted text
            input.setEditable(false);
            this.encryptedText = input;
        }


        textField.getChildren().addAll(fieldName,input);
        textField.setStyle("-fx-border-color: gray");
        return textField;

    }

    public VBox configureRadioLayout(String labelText,String firstText,String secondText){
        //Title part
        VBox methodLayout = new VBox();
        methodLayout.setSpacing(0);
        methodLayout.setPrefWidth(130);
        methodLayout.setPrefHeight(40);
        methodLayout.setStyle("-fx-border-color: black");



        //label to border
        Label label = new Label();
        label.setText(labelText);
        label.setPadding(new Insets(5,0,-10,10));

        //radio button part

        HBox radioButtons = new HBox();
        radioButtons.setSpacing(20);


        //creating radio buttons
        RadioButton radioButton1 = new RadioButton();
        radioButton1.setText(firstText);
        radioButton1.setSelected(true);

        RadioButton radioButton2 = new RadioButton();
        radioButton2.setText(secondText);

        //Toggle group
        ToggleGroup group = new ToggleGroup();
        radioButton1.setToggleGroup(group);
        radioButton2.setToggleGroup(group);


        if (labelText.equals("Method")) {
            this.AES = radioButton1;
            this.DES = radioButton2;
        } else if (labelText.equals("Mode")) {
            this.CBC = radioButton1;
            this.OFB = radioButton2;
        }





        //adding buttons to horizontal layout
        radioButtons.getChildren().addAll(radioButton1,radioButton2);
        radioButtons.setPadding(new Insets(5,0,0,0));

        //main construct of radio layout
        methodLayout.getChildren().addAll(label,radioButtons);
        methodLayout.setMargin(radioButtons,new Insets(10,10,10,10));

        return methodLayout;

    }

    public HBox createConnectionButtons(String iconSource1,String iconSource2){
        HBox connectionLayout = new HBox();
        connectionLayout.setSpacing(10);


        //creating images
        Image connectionIcon = new Image(iconSource1);
        Image disconnectionIcon = new Image(iconSource2);

        //create buttons and add images to it
        Button connectButton = new Button();
        this.connect = connectButton;
        connectButton.setOnAction(this);
        connectButton.setText("Connect");
        connectButton.setGraphic(new ImageView(connectionIcon));
        connectButton.setPrefHeight(30);
        connectButton.setPrefWidth(120);


        Button disconnectButton = new Button();
        this.disconnect = disconnectButton;
        disconnectButton.setOnAction(this);
        disconnectButton.setDisable(true);
        disconnectButton.setText("Disconnect");
        disconnectButton.setGraphic(new ImageView(disconnectionIcon));
        disconnectButton.setPrefHeight(30);
        disconnectButton.setPrefWidth(130);

        connectionLayout.getChildren().addAll(connectButton,disconnectButton);



        return connectionLayout;

    }

    public Button createButton(String text,boolean iconable,String iconSource){
        Button button = new Button();
        button.setText(text);
        if(iconable){
            Image icon = new Image(iconSource);
            button.setGraphic(new ImageView(icon));
        }

        button.setOnAction(this);



        return button;
    }

    public static void printChatBox(String message){
        chatBox.setText(chatBox.getText()+message+"\n");
    }

    public static void controlRadioButtons(){
        if(Client.AES.isSelected()){
            //AES
            Client.method = "AES";
            if(Client.CBC.isSelected()){
                //AES CBC
                Client.mode = "CBC";
            }else if(Client.OFB.isSelected()){
                //AES OFB
                Client.mode = "OFB";
            }
        }else if (Client.DES.isSelected()) {
            //DES
            Client.method = "DES";
            if(Client.CBC.isSelected()){
                //DES CBC
                Client.mode = "CBC";
            }else if(Client.OFB.isSelected()){
                //DES OFB
                Client.mode = "OFB";
            }
        }
    }

    public void onWindowCloseHandler(){
        try{
            dataOut.close();
        }catch (NullPointerException e){
            //System.out.println("writer is already closed or not opened yet!");
        }
        try {
            clientThread.interrupt();
            socket.close();
        } catch (NullPointerException | IOException e) {
            //System.out.println("thread already stopped,writer and socket is also closed, program is stopping");
            e.printStackTrace();
        }

    }

    @Override
    public void start(Stage primaryStage){
        //window name initialization
        window = primaryStage;
        window.setTitle("Crypto Messenger");

        //------------------------------------------------------
        //bottom Layout init
        VBox bottomLayout = new VBox();
        bottomLayout.setSpacing(5);

        //HBox
        HBox bottomTextPart = new HBox();
        bottomTextPart.setSpacing(0);
        bottomTextPart.setStyle("-fx-border-color: gray");

        //creating buttons
        encrypt = createButton("Encrypt",false, "send.png");
        encrypt.setAlignment(Pos.CENTER);
        encrypt.setPrefWidth(100);
        encrypt.setPrefHeight(25);

        sendMessage = createButton("Send",true, "send.png");
        sendMessage.setAlignment(Pos.CENTER);
        sendMessage.setPrefWidth(100);
        sendMessage.setPrefHeight(35);
        sendMessage.setDisable(true);


        //connection status
        status = new Label();
        status.setText("Not Connected");

        //creating TextField VBoxes
        VBox input = creatingBottomTextFields("Text",true);
        VBox output = creatingBottomTextFields("Crypted Text",false);


        bottomTextPart.getChildren().addAll(input,output,encrypt,sendMessage);


        bottomTextPart.setMargin(encrypt,new Insets(50,0,0,0));
        bottomTextPart.setMargin(sendMessage,new Insets(45,0,0,0));

        bottomLayout.getChildren().addAll(bottomTextPart,status);
        bottomLayout.setMargin(status,new Insets(0,0,5,5));


        //------------------------------------------------------
        //creating Main TextField


        chatBox = new TextArea();
        chatBox.setPrefWidth(700);
        chatBox.setPrefHeight(550);
        chatBox.setEditable(false);



        //--------------------------------------------------
        //upper menu layout configuring

        HBox upperMenu = new HBox();
        upperMenu.setSpacing(20);


        VBox methodLayout = configureRadioLayout("Method","AES","DES");
        VBox modeLayout = configureRadioLayout("Mode","CBC","OFB");
        HBox connectionLayout = createConnectionButtons("play.png","stop.png");

        //adding child layouts to upperMenu layout
        upperMenu.getChildren().addAll(connectionLayout,methodLayout,modeLayout);
        upperMenu.setMargin(connectionLayout,new Insets(15,10,0,80));
        upperMenu.setStyle("-fx-border-color: gray");
        upperMenu.setPadding(new Insets(10,0,10,0));


        //----------------------------------------------



        //Server text

        StackPane pane = new StackPane();

        Label mainText = new Label();
        mainText.setText("Server");
        mainText.setPadding(new Insets(0,0,-30,10));



        pane.getChildren().add(mainText);
        pane.setAlignment(Pos.CENTER_LEFT);


        //----------------------------------------------




        //main construction of layout

        BorderPane mainLayout = new BorderPane();
        VBox upperPart = new VBox();
        upperPart.setSpacing(30);
        upperPart.getChildren().addAll(pane,upperMenu);



        mainLayout.setBottom(bottomLayout);
        mainLayout.setTop(upperPart);
        mainLayout.setCenter(chatBox);


        //init alertbox
        alertBox = new UserNameAlertBox(this);

        //set scene and show windows
        Scene scene = new Scene(mainLayout,700,768);
        window.setScene(scene);
        window.setOnCloseRequest((event) ->{
            onWindowCloseHandler();
        });
        window.show();
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource().equals(this.encrypt)) {
            controlRadioButtons();

            //get text inside textbox and encrypt
            String message = this.userInput.getText();
            String encryptedMessage="";
            try {
                if(Client.method.equals("AES")){
                    encryptedMessage = MyCrypt.encryption(String.format("%s/%s/PKCS5PADDING", Client.method, Client.mode), Client.AESKey,message, Client.initVectorAES);
                }else if(Client.method.equals("DES")){
                    encryptedMessage = MyCrypt.encryption(String.format("%s/%s/PKCS5PADDING", Client.method, Client.mode), Client.DESKey,message, Client.initVectorDES);
                }
            } catch (Exception e) {
                System.out.println("Error while encryption operation");
                e.printStackTrace();
            }


            //put encrypted message to encrypted message box
            this.encryptedText.setText(encryptedMessage);
            this.userInput.setText("");
            //activate send button
            this.sendMessage.setDisable(false);
        } else if (event.getSource().equals(this.sendMessage)) {
            if(connected){
                this.sendMessage.setDisable(true);
                try {
                    //real sending operation via sockets
                    ReceiveMessageThread.sendMessage(this.encryptedText.getText(),this.socket);
                } catch (IOException e) {
                    e.printStackTrace();
                    //System.out.println("Error on sending message");
                }
            }else{
                //System.out.println("not connected");
            }
        } else if (event.getSource().equals(this.connect)) {
            try {
                //display username input box
                alertBox.display();
            } catch (Exception e) {
                e.printStackTrace();
                //System.out.println("error on showing username input box!");
            }
        } else if (event.getSource().equals(this.disconnect)) {
            //System.out.println("Disconnected from server");
            this.connected=false;
            //status part for client user friendly experience
            status.setText("Not Connected");

            //closing connection via closing stream and socket
            try {
                dataOut.close();
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    //System.out.println("Error while closing socket connections");
                }
                this.connect.setDisable(false);
                this.disconnect.setDisable(true);
            }catch (NullPointerException e){
                //did not send any message and disconnected
                try {
                    socket.close();
                } catch (IOException f) {
                    f.printStackTrace();
                    //System.out.println("Error while closing socket connections");
                }
                //configure boolean values
                this.connect.setDisable(false);
                this.disconnect.setDisable(true);
            }
        }else if (event.getSource().equals(okay)) {
            boolean isAlphaNumeric = (userNameInput.getText() != null && !(userNameInput.getText().equals("")) )&& userNameInput.getText().chars().allMatch(Character::isLetterOrDigit);
            if(isAlphaNumeric){
                // username is alphanumeric, which means correct, if not alphanumeric or empty string then program does not accept it
                //-------------------
                this.connected = true;
                this.connect.setDisable(true);
                this.disconnect.setDisable(false);
                //-------------------
                userName = userNameInput.getText();
                status.setText("Connected : "+userName);
                alertBox.closeWindow();
                controlRadioButtons();
                try {
                    this.socket = ReceiveMessageThread.Client();
                } catch (IOException e) {
                    e.printStackTrace();
                    //System.out.println("Error on Connect");
                }
            }else{
                //invalid username
            }
        } else if (event.getSource().equals(cancel)) {
            alertBox.closeWindow();
        }
    }



}
