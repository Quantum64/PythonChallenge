import React, { useRef, useState } from 'react';
import './App.css';
import Editor from '@monaco-editor/react';
import skulpt from 'skulpt';

function App() {
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

  function handleShowValue() {
    let result = ""
    skulpt.pre = "output"
    skulpt.configure({ output: (text) => {
      result += text
    }, read: readf });
    let promise = skulpt.misceval.asyncToPromise(function () {
      return skulpt.importMainWithBody("<stdin>", false, editor.current(), true);
    });
    promise.then((mod) => {
      setOutputText(result)
    });
  }

  return (
    <>
      <button onClick={handleShowValue} disabled={!isEditorReady}>
        Run code
      </button>

      <Editor
        height="60vh"
        language="python"
        value={"print 'Hello, World'"}
        editorDidMount={handleEditorDidMount}
      />
      <Editor
        height="30vh"
        language="text"
        value={outputText}
        onChange={() => {}}
        editorDidMount={handleOutputDidMount}
      />
    </>
  );
}

export default App;
