import React, { useRef, useState } from 'react';
import Challenge from './Challenge'

import Grid from '@material-ui/core/Grid';
import Button from '@material-ui/core/Button';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import CssBaseline from '@material-ui/core/CssBaseline';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';
import DialogTitle from '@material-ui/core/DialogTitle';
import Dialog from '@material-ui/core/Dialog';
import TextField from '@material-ui/core/TextField';

function App() {

  let content = <Challenge></Challenge>

  return (
    <div style={{ height: "100%" }}>
      <CssBaseline />
      <AppBar position="static">
        <Toolbar>
          <Grid container spacing={16} alignItems="center">
            <Grid item>
              <Typography variant="h6" color="inherit" noWrap>
                Python Challenge
            </Typography>
            </Grid>
          </Grid>
        </Toolbar>
      </AppBar>
      <Grid container justify="center" style={{ padding: 20, height: "90%" }}>
        <Grid item xs>
          {content}
        </Grid>
      </Grid>
    </div>
  )
}

export default App;
