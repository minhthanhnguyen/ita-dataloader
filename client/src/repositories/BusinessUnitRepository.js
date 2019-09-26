const axios = require('axios')

export default class BusinessUnitRepository {
  async _getBusinessUnits () {
    let businessUnitResponse = await axios.get('/api/business-units')
    return businessUnitResponse.data
  }

  async _saveTariffs (containerName, fileName, bytes) {
    // const saveResponse = await axios.put('/api/tariffs/save', { csv }, {
    //   params: {
    //     countryCode
    //   }
    // })
    // return saveResponse.data
  }
}
