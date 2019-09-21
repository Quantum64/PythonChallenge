import React, { useRef, useState } from 'react';
import MonacoEditor from 'react-monaco-editor';
import skulpt from 'skulpt';

import Grid from '@material-ui/core/Grid';
import Button from '@material-ui/core/Button';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import CssBaseline from '@material-ui/core/CssBaseline';
import TextField from '@material-ui/core/TextField';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';


const URL = "python.q64.co:12345/socket"
const automaticLayout = false;


class App extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      code: "",
      output: "",
      question: "No question",
      time: 1,
      phase: "",
      test: "",
      feedback: "",
      result: "",
      username: "",
      score: 0,
      totslScore: 0,
      pass: false,
      submitted: false,
      named: false
    }

    this.timer = 0;
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
        time: message.time,
        output: "",
        code: message.starter,
        submitted: false
      });
      this.startTime = message.time;
      this.startTimer();
    } else if (message.type === "score") {
      this.setState({
        feedback: message.feedback,
        result: message.result,
        test: message.test,
        pass: message.pass,
        score: message.score,
        totalScore: message.totalScore,
        phase: "score",
        results: []
      })
    } else if (message.type === "results") {
      this.setState({
        results: message.results
      })
    }
  }

  startTimer() {
    if (this.startTime > 0) {
      this.timer = setInterval(() => this.countDown(), 1000);
    }
  }

  countDown() {
    let seconds = this.state.time - 1;
    this.setState({
      time: seconds,
    });
    if (seconds == 0) {
      clearInterval(this.timer);
      this.setState({
        submitted: true
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
    }));
    this.setState({
      submitted: true
    });

    // TODO snackbar
  }

  handleUsername() {
    this.socket.send(JSON.stringify({
      type: "username",
      username: this.state.username
    }));
    this.setState({
      named: true
    });
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

  getResultTable() {
    const rows = [];
    for (let result of this.state.results) {
      rows.push(
        <TableRow key={result}>
          <TableCell>
            {result.name}
          </TableCell>
          <TableCell>
            {result.last}
          </TableCell>
          <TableCell>
            {result.total}
          </TableCell>
        </TableRow>);
    }
    return (
      <div>
        <Typography variant="h2">
          Problem Results
        </Typography>
        <Paper>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Username</TableCell>
                <TableCell>Score</TableCell>
                <TableCell>Total Score</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {rows}
            </TableBody>
          </Table>
        </Paper>
      </div>
    );
  }


  render() {
    const options = { selectOnLineNumbers: true, automaticLayout: automaticLayout };
    const optionsArguments = { ...options, minimap: { enabled: false } }
    const optionsDisabled = { ...optionsArguments, readOnly: true }
    const bytes = 0

    let content = <div>
      Connecting to the socket. Please wait.
      <br />
      If you see this message for more than a few seconds, try refreshing the page.
    </div>;

    if (this.state.phase === "waiting") {
      content =
        <Grid container direction="column" justify="center" alignItems="center" style={{ width: "100%", height: "100%" }}>
          <Grid item>
            <Typography variant="h1" component="h2">
              WAITING FOR PROBLEM
            </Typography>
          </Grid>
          {this.state.named ?
            <Grid item>
              <Typography variant="h6">
                Greetings, {this.state.username}
              </Typography>
            </Grid>
            :
            <React.Fragment>
              <Grid item>
                <Typography variant="h6">
                  While you're waiting, pick a username
            </Typography>
              </Grid>
              <Grid item>
                <Grid container justify="center" alignItems="center" spacing={2}>
                  <Grid item>
                    <TextField
                      id="outlined-name"
                      label="Name"
                      value={this.state.username}
                      onChange={(event) => {
                        this.setState({
                          username: event.target.value
                        })
                      }}
                      margin="normal"
                      variant="outlined"
                    />
                  </Grid>
                  <Grid item>
                    <Button size="small" variant="contained" color="primary" onClick={() => this.handleUsername()}>
                      Submit
                </Button>
                  </Grid>
                </Grid>
              </Grid>
            </React.Fragment>}
        </Grid>
    }
    else if (this.state.phase === "question") {
      content =
        <React.Fragment>
          <Typography variant="h5">
            {this.state.question}
          </Typography>
          <Grid container direction="column" spacing={0} style={{ height: "100%" }}>
            <Grid item>
              <Typography variant="h6">
                Code
              </Typography>
            </Grid>
            <Grid item xs>
              <MonacoEditor width="100%" height="300px" language="python" theme="vs"
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
                  {this.state.submitted ? <span></span> :
                    <Grid item>
                      <Button size="small" variant="contained" color="secondary" onClick={() => {
                        this.handleSubmit();
                      }}>
                        Submit Solution
                     </Button>
                    </Grid>
                  }
                  <Grid item>
                    <Typography variant="h6">
                      Time Remaining: {this.state.time}
                    </Typography>
                  </Grid>
                </Grid>
              </Grid>
            </Grid>
            <Grid item xs>
              <MonacoEditor width="100%" height="250px" language="text" theme="vs"
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
          <Grid container justify="center" alignItems="center" direction="column">
            <Grid item>
              {this.state.pass ?
                <Typography variant="h3" style={{ color: "green", fontSize: "10em" }}>
                  PASS
              </Typography> :
                <Typography variant="h3" style={{ color: "red", fontSize: "10em" }}>
                  FAIL
                </Typography>}
            </Grid>
            <Grid item>
              <Typography variant="h3" style={{ color: "RoyalBlue" }}>
                Your score for this problem is {this.state.score}
              </Typography>
            </Grid>
            <Grid item>
              <Typography variant="h5" style={{ color: "RoyalBlue" }}>
                This brings your total score to {this.state.totalScore}
              </Typography>
            </Grid>
          </Grid>
          <br /><br />
          <Typography variant="h3">
            Run Details
          </Typography>
          <Typography variant="h6">
            Feedback: {this.state.feedback}
          </Typography>
          <Typography variant="h6">
            Your test case: {this.state.test}
          </Typography>
          <Typography variant="h6">
            Your program produced: {this.state.result}
          </Typography>
        </React.Fragment>
    } else if (this.state.phase === "results") {
      content = this.getResultTable()
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
