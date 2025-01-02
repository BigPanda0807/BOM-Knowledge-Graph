import {
    post,
    get
} from '@/utils/request.js'

const addEquipment = (data) => {
    return post('/api/equipment/add', data)
}

const getAllEquipment = () => {
    return get('/api/equipment/getList')
}

const editEquipment = (data) => {
    return post('/api/equipment/edit', data)
}

const deleteEquipment = (id) => {
    return get('/api/equipment/delete', {
        id
    })
}

const showRelationship = (code) => {
    return get('/api/equipment/showRelationship', {
        code
    })
}

const releaseException = (id, code, status) => {
    return get('/api/equipment/releaseException', {
        id, code, status
    })
}

const reportException = (id, code, status) => {
    return get('/api/equipment/reportException', {
        id, code, status
    })
}

export { addEquipment, getAllEquipment, editEquipment, deleteEquipment, showRelationship, releaseException, reportException }