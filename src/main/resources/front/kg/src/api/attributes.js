import {
    post,
    get
} from '@/utils/request.js'

const addAttributes = (data) => {
    return post('/api/attributes/add', data)
}

const getAllAttributes = () => {
    return get('/api/attributes/getList')
}

const editAttributes = (data) => {
    return post('/api/attributes/edit', data)
}

const deleteAttributes = (id) => {
    return get('/api/attributes/delete', {
        id
    })
}

export { addAttributes, getAllAttributes, editAttributes, deleteAttributes }