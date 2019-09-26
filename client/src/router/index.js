import Vue from 'vue'
import Router from 'vue-router'
import Upload from '@/components/Upload'
import Config from '@/components/Config'
import BusinessUnitRepository from '../repositories/BusinessUnitRepository'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Upload',
      component: Upload,
      props: {
        businessUnitRepository: new BusinessUnitRepository()
      }
    }, {
      path: '/config',
      name: 'Config',
      component: Config,
      props: {
        businessUnitRepository: new BusinessUnitRepository()
      }
    }
  ]
})
