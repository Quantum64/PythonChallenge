import React, { useRef, useState } from 'react';
import Editor from '@monaco-editor/react';
import skulpt from 'skulpt';

import Grid from '@material-ui/core/Grid';
import Button from '@material-ui/core/Button';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import CssBaseline from '@material-ui/core/CssBaseline';


const URL = "localhost:12345/socket"
class App extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      isEditorReady: false,
      outputText: "",
      question: "No question",
      time: 100,
      phase: "question"
    }
    this.editor = React.createRef();
    this.output = React.createRef();

    this.socket = new WebSocket("ws://" + URL);
    this.socket.onmessage = (msg) => this.message(msg);
    //this.socket.onclose = function () { alert("WebSocket connection closed") };
  }

  handleEditorDidMount(e) {
    this.setState({
      phase: "waiting",
      isEditorReady: true
    });
    this.editor.current = e;
  }

  handleOutputDidMount(e) {
    this.output.current = e;
  }

  message(msg) {
    console.log(msg.data)
    const message = JSON.parse(msg.data);
    if (message.type === "phase") {
      this.setState({ phase: message.phase });
    } else if (message.type === "question") {

    }
  }

  readf(x) {
    if (skulpt.builtinFiles === undefined || skulpt.builtinFiles["files"][x] === undefined)
      throw "File not found: '" + x + "'";
    return skulpt.builtinFiles["files"][x];
  }

  handleRunCode() {
    let result = ""
    skulpt.pre = "output"
    skulpt.configure({
      output: (text) => {
        result += text
      }, read: this.readf
    });
    let promise = skulpt.misceval.asyncToPromise(function () {
      return skulpt.importMainWithBody("<stdin>", false, this.editor.current(), true);
    });
    promise.then((mod) => {
      this.setState({
        outputText: result
      })
    });
  }

  render() {
    let content = <div>
      Unable to connect to the socket...
    </div>;

    if (this.state.phase === "waiting") {
      content = <h1>
        WAITING FOR QUESTION
      </h1>
    }
    else if (this.state.phase === "question") {
      content = <>
        <Editor
          height="40vh"
          language="python"
          value={"print 'Hello, World'"}
          editorDidMount={(e) => this.handleEditorDidMount(e)}
        />

        <Grid container direction="row" alignItems="center" spacing={2} style={{ padding: 5 }}>
          <Grid item>
            <Typography variant="h6">
              Program Output
            </Typography>
          </Grid>
          <Grid item>
            <Button size="small" variant="contained" color="primary" onClick={() => this.handleRunCode()}>
              Run Code
             </Button>
          </Grid>
          <Grid item>
            <Button size="small" variant="contained" color="secondary" onClick={() => {

            }}>
              Submit Solution
           </Button>
          </Grid>
        </Grid>

        <Editor
          height="30vh"
          language="text"
          value={(e) => this.stateoutputText(e)}
          onChange={() => { }}
          editorDidMount={(o) => this.handleOutputDidMount(o)}
        />
      </>
    }
    else if (this.state.phase === "score") {

    }

    return (
      <div style={{ height: "100%" }}>
        <CssBaseline />
        <AppBar position="static">
          <Toolbar>
            <Grid container spacing={2} alignItems="center">
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
}

export default App;
