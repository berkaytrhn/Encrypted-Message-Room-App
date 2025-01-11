import { Box, Paper } from "@mui/material";



const ChatListView = () => {


    const listOfChats = [
        {
            name: "Chat 1",
        },
        {
            name: "Chat 2",
        },
        {
            name: "Chat 3",
        },
        {
            name: "Chat 4",
        },
        {
            name: "Chat 5",
        }

    ]

    return (
        <Box>
        {listOfChats.map((element)=>{
            return <Paper><p>{element.name}</p></Paper>
        })}
        </Box>
    );
}


export default ChatListView;