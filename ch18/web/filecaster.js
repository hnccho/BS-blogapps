
function onload() {
   document.forms[0].add_button.disabled = true;
}
 
function validate() {
   if (document.forms[0].remoteUrl.value.length > 0) {
      document.forms[0].add_button.disabled = false;
      document.forms[0].fileUpload.disabled = true;
   } else if (document.forms[0].fileUpload.value.length > 0) {
      document.forms[0].add_button.disabled = false;
      document.forms[0].remoteUrl.disabled  = true;
   } else {
      document.forms[0].add_button.disabled = true;
      document.forms[0].fileUpload.disabled = false;
      document.forms[0].remoteUrl.disabled  = false;
   }
}

function removeFileCast(filename) {
   document.forms[1].removeFile.value = filename;
   document.forms[1].submit();
}

   