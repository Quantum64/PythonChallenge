import React, { useRef, useState } from 'react';
import Editor from '@monaco-editor/react';
import skulpt from 'skulpt';

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

function Challenge() {
    const [isEditorReady, setIsEditorReady] = useState(false);
    const [outputText, setOutputText] = useState("");
    const editor = useRef();
    const output = useRef();

    function handleEditorDidMount(e) {
        setIsEditorReady(true);
        editor.current = e;
    }

    function handleOutputDidMount(e) {
        output.current = e;
    }

    function readf(x) {
        if (skulpt.builtinFiles === undefined || skulpt.builtinFiles["files"][x] === undefined)
            throw "File not found: '" + x + "'";
        return skulpt.builtinFiles["files"][x];
    }

    function handleRunCode() {
        let result = ""
        skulpt.pre = "output"
        skulpt.configure({
            output: (text) => {
                result += text
            }, read: readf
        });
        let promise = skulpt.misceval.asyncToPromise(function () {
            return skulpt.importMainWithBody("<stdin>", false, editor.current(), true);
        });
        promise.then((mod) => {
            setOutputText(result)
        });
    }

    return (
        <>
            <Editor
                height="40vh"
                language="python"
                value={"print 'Hello, World'"}
                editorDidMount={handleEditorDidMount}
            />

            <Grid container direction="row" alignItems="center" spacing={16} style={{ padding: 5 }}>
                <Grid item>
                    <Typography variant="h6">
                        Program Output
                        </Typography>
                </Grid>
                <Grid item>
                    <Button size="small" variant="contained" color="primary" onClick={handleRunCode}>
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
                value={outputText}
                onChange={() => { }}
                editorDidMount={handleOutputDidMount}
            />
        </>
    );
}

export default Challenge;