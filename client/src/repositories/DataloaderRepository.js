const axios = require('axios')

export default class DataloaderRepository {
  async _save (containerName, file) {
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

  async _getBusinessUnits () {
    let businessUnitResponse = await axios.get('/api/business-units')
    return businessUnitResponse.data
  }

  async _getDataloaderConfig (containerName) {
    let dataSetConfigsResponse = await axios.get('/api/configuration', {
      params: {
        containerName
      }
    })

    return dataSetConfigsResponse.data
  }

  async _saveDataloaderConfig (dataloaderConfig, containerName) {
    let configSaveResponse = await axios({
      url: '/api/save/configuration',
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      params: {
        containerName
      },
      data: dataloaderConfig
    })
    return configSaveResponse
  }

  async _getStorageMetadata (containerName) {
    let storageMetadataResponse = await axios.get('/api/storage-metadata', {
      params: {
        containerName
      }
    })

    return storageMetadataResponse.data
  }

  async _startIngestProcess (containerName) {
    let ingestProcessResponse = await axios.get('/api/ingest', {
      params: {
        containerName
      }
    })

    return ingestProcessResponse
  }

  async _getIngestStatus (containerName) {
    let ingestStatusResponse = await axios.get('/api/ingest/status', {
      params: {
        containerName
      }
    })
    return ingestStatusResponse.data
  }
}
