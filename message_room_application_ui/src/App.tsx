import React from 'react';
import logo from './logo.svg';
import './App.css';
import { Box, Grid2 as Grid } from '@mui/material';
import ChatListView from './components/ChatListView';
import ChatView from './components/ChatView';

function App() {
  return (
    <div id={"main_application"}>
      <Grid container direction="row" className={"widthFull heightFull"}>
        <Grid size={12} className={"topbar"}>
          <Box>
            <p>Message Room Application</p>
          </Box>

        </Grid>

        <Grid 
          container 
          size={12} 
          direction="row" 
          className={"widthFull heightFull"}>
            
          <Grid size={2} className={"chatsView"}>
            <ChatListView></ChatListView>
          </Grid>
          <Grid size={10} className={"chatView"}>
            <ChatView></ChatView>
          </Grid>

        </Grid>
      </Grid >
    </div>

  );
}

export default App;
