import { createApp } from 'vue'
import { create, NButton, NInput, NTable, NCard, NForm, NFormItem, NDatePicker, NSelect, NModal, NMessageProvider, NDialogProvider, NSpace, NDescriptions, NDescriptionsItem, NTag, NDivider, NGrid, NGridItem, NInputNumber, NRow, NCol, NIcon } from 'naive-ui'
import App from './App.vue'
import router from './router'
import './style.css'

const naive = create({
  components: [NButton, NInput, NTable, NCard, NForm, NFormItem, NDatePicker, NSelect, NModal, NMessageProvider, NDialogProvider, NSpace, NDescriptions, NDescriptionsItem, NTag, NDivider, NGrid, NGridItem, NInputNumber, NRow, NCol, NIcon]
})

const app = createApp(App)
app.use(naive)
app.use(router)
app.mount('#app')
