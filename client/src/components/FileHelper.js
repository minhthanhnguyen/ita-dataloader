
export const readUploadedFileAsArrayBuffer = (inputFile) => {
  const temporaryFileReader = new window.FileReader()
  return new Promise((resolve, reject) => {
    temporaryFileReader.onerror = () => {
      temporaryFileReader.abort()
    }

    temporaryFileReader.onload = () => {
      resolve(temporaryFileReader.result)
    }

    temporaryFileReader.readAsArrayBuffer(inputFile)
  })
}
