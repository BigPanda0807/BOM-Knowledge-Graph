import {
    post,
    get
} from '@/utils/request.js'

const renderBomMap = () => {
    return get('/api/bom/renderMap')
}

const getAllBom = () => {
    return get('/api/bom/getAllBom')
}

const getFinishedProductByCode = (code) => {
    return get("/api/bom/getFinishedProductByCode", {
        code
    })
}

const getBomNodeAndRelationship = (code) => {
    return get("/api/bom/getBomNodeAndRelationship", {
        code
    })
}

export { renderBomMap, getAllBom, getFinishedProductByCode, getBomNodeAndRelationship }