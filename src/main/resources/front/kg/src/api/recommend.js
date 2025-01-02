import {
    post,
    get
} from '@/utils/request.js'

const getAllRecommend = () => {
    return get('/api/recommend/getList')
}

const deleteRecommend = (id) => {
    return get('/api/recommend/delete', {
        id
    })
}

const addRecommend = (recommendName) => {
    return get('/api/recommend/add', {
        recommendName
    })
}

const getRecommendById = (id) => {
    return get("/api/recommend/getRecommendById", {
        id
    })
}

const updateRecommend = (data) => {
    return post('/api/recommend/input/update', data)
}

const updateRecommendResult = (data) => {
    return post('/api/recommend/updateRecommendResult', data)
}

const doInput = (id) => {
    return get("/api/recommend/doInput", {
        id
    })
}

const getInput = (id) => {
    return get("/api/recommend/getInput", {
        id
    })
}

const getStructureRecommend = (id) => {
    return get("/api/recommend/getStructureRecommend", {
        id
    })
}

const getRecommendFinishedProduct = (code) => {
    return get("/api/recommend/getRecommendFinishedProduct", {
        code
    })
}

const sendRecommendResultReturnToKG = (id) => {
    return get("/api/recommend/recommendResultReturnToKG", {
        id
    })
}

export { getAllRecommend, deleteRecommend, addRecommend, getRecommendById, updateRecommend, doInput, getInput, getStructureRecommend, updateRecommendResult, getRecommendFinishedProduct, sendRecommendResultReturnToKG }