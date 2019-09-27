const axios = require('axios')

export default class DataloaderRepository {

  async _save(containerName, file) {
    let fileSaveResponse = await axios({
      url: '/api/save/file',
      method: 'PUT',
      params: {
        containerName
      },
      data: file
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
