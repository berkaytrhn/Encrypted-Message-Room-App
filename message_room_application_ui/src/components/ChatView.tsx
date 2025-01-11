import { Grid2 as Grid, TextareaAutosize, TextField } from "@mui/material";
import { ChangeEvent, ReactNode, useState } from "react";


interface ChatViewProps {
    children?: ReactNode;
}

const ChatView = (props: ChatViewProps) => {

    const [message, setMessage] = useState("Example Message");
    const [textValue, setTextValue] = useState("");


    const handleTextChange = (e: any) => {
        setTextValue(e.target.value);
    }

    return (
        <Grid 
            container 
            direction="column" 
            className={"widthFull heightFull"}>
            <Grid size={2}>
                <TextareaAutosize 
                    className={"widthFull heightFull"} 
                    autoFocus={false} 
                    contentEditable={false} 
                    value={message}/>
            </Grid>
            <Grid size={10}>
                <TextField
                    multiline
                    value={textValue}
                    onChange={handleTextChange}
                />
            </Grid>

        </Grid>
    );
}


export default ChatView;