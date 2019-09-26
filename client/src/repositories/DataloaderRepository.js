const axios = require('axios')

export default class DataloaderRepository {

  async _save(destinationFileName, fileArrayBuffer, containerName) {
    let fileContent = String.fromCharCode.apply(null, new Uint8Array(fileArrayBuffer));
    let base64FileContent = btoa(fileContent);
    let fileSaveResponse = await axios.put('/api/save/file', {
      destinationFileName,
      base64FileContent,
      containerName
    })
    return fileSaveResponse.data
  }

  async _getBusinessUnits() {
    let businessUnitResponse = await axios.get('/api/business-units')
    return businessUnitResponse.data
  }

  async _getDataSetConfigs(containerName) {
    console.log(containerName);
  }
}
