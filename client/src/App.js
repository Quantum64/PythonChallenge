import React, { useRef, useState } from 'react';
import MonacoEditor from 'react-monaco-editor';
import skulpt from 'skulpt';

import Grid from '@material-ui/core/Grid';
import Button from '@material-ui/core/Button';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import CssBaseline from '@material-ui/core/CssBaseline';


const URL = "localhost:12345/socket"
const automaticLayout = false;


class App extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      code: "",
      output: "",
      question: "No question",
      time: 100,
      phase: "",
      test: "",
      feedback: "",
      result: ""
    }

    this.socket = new WebSocket("ws://" + URL);
    this.socket.onmessage = (msg) => this.message(msg);
    //this.socket.onclose = function () { alert("WebSocket connection closed") };
  }

  message(msg) {
    console.log(msg.data)
    const message = JSON.parse(msg.data);
    if (message.type === "phase") {
      this.setState({ phase: message.phase });
    } else if (message.type === "question") {
      this.setState({
        question: message.question,
        time: message.time
      });
    } else if (message.type === "score") {
      this.setState({
        feedback: message.feedback,
        result: message.result,
        test: message.test
      })
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
    skulpt.importMainWithBody("<stdin>", false, this.state.code, true);
    this.setState({
      output: result
    })
  }

  handleSubmit() {
    this.socket.send(JSON.stringify({
      type: "submit",
      submission: this.state.code
    }))

    alert("Submitted!");
  }

  mutateEditor(monaco) {
    /*
    this.hack = monaco;
    monaco.editor.defineTheme('emotion', {
      base: 'vs',
      inherit: true,
      rules: [
        { token: 'keyword', foreground: '0000ff' },
      ]
    });
    monaco.languages.register({
      id: "elang"
    });
    */
  }


  render() {
    const options = { selectOnLineNumbers: true, automaticLayout: automaticLayout };
    const optionsArguments = { ...options, minimap: { enabled: false } }
    const optionsDisabled = { ...optionsArguments, readOnly: true }
    const bytes = 0

    let content = <div>
      Unable to connect to the socket...
    </div>;

    if (this.state.phase === "waiting") {
      content = <h1>
        WAITING FOR QUESTION
      </h1>
    }
    else if (this.state.phase === "question") {
      content =
        <React.Fragment>
          <h6>
            {this.state.question}
          </h6>
          <Grid container direction="column" spacing={0} style={{ height: "100%" }}>
            <Grid item>
              <Typography variant="h6">
                Code ({bytes} bytes)
            </Typography>
            </Grid>
            <Grid item xs>
              <MonacoEditor width="100%" height="100%" language="python" theme="vs"
                value={this.state.code}
                options={options}
                onChange={(value, event) => {
                  this.setState({
                    code: value
                  });
                }}
                editorWillMount={(editor) => this.mutateEditor(editor)}
              />
            </Grid>
            <br />
            <Grid item>
              <Grid container direction="row" alignItems="center" spacing={2} style={{ padding: 5 }}>
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
                      this.handleSubmit();
                    }}>
                      Submit Solution
                   </Button>
                  </Grid>
                </Grid>
              </Grid>
            </Grid>
            <Grid item xs>
              <MonacoEditor width="100%" height="100%" language="text" theme="vs"
                value={this.state.output}
                options={optionsDisabled}
              />
            </Grid>
          </Grid>
        </React.Fragment>
    }
    else if (this.state.phase === "score") {
      content =
        <React.Fragment>
          <h3>Feedback</h3>
          <h2>{this.state.feedback}</h2>
          <br></br>
          <h3>Your test case: </h3>
          <h2>{this.state.test}</h2>
          <h3>Your program produced</h3>
          <h2>{this.state.result}</h2>
        </React.Fragment>
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
