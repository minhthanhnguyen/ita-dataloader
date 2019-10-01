import Vue from 'vue'
import Router from 'vue-router'
import Upload from '@/components/Upload'
import Config from '@/components/Config'
import Log from '@/components/Log'
import DataloaderRepository from '../repositories/DataloaderRepository'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Upload',
      component: Upload,
      props: {
        dataloaderRepository: new DataloaderRepository()
      }
    },{
      path: '/config/:containerName',
      name: 'Config',
      component: Config,
      props: {
        dataloaderRepository: new DataloaderRepository()
      }
    },{
      path: '/log/:containerName',
      name: 'Log',
      component: Log,
      props: {
        dataloaderRepository: new DataloaderRepository()
      }
    }
  ]
})
