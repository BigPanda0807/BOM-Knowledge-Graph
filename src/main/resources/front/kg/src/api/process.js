import {
    post,
    get
} from '@/utils/request.js'

const addProcess = (data) => {
    return post('/api/process/add', data)
}

const getAllProcess = () => {
    return get('/api/process/getList')
}

const editProcess = (data) => {
    return post('/api/process/edit', data)
}

const deleteProcess = (id) => {
    return get('/api/process/delete', {
        id
    })
}

const predictEquipment = (data) => {
    return post('/api/process/predictEquipment', data)
}

export { getAllProcess, deleteProcess, addProcess, editProcess, predictEquipment }