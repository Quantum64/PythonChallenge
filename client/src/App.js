import React, { useRef, useState } from 'react';
import './App.css';
import Editor from '@monaco-editor/react';

function App() {
  const [isEditorReady, setIsEditorReady] = useState(false);
  const editor = useRef();
  const editor = useRef();

  function handleEditorDidMount(e) {
    setIsEditorReady(true);
    editor.current = e;
  }

  function handleShowValue() {
    alert(editor.current());
  }

  return (
    <>
      <button onClick={handleShowValue} disabled={!isEditorReady}>
        Show value
      </button>

      <Editor
        height="90vh"
        language="python"
        value={"// write your code here"}
        editorDidMount={handleEditorDidMount}
      />
    </>
  );
}

export default App;
