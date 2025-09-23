declare namespace API {
  type DeleteRequest = {
    id?: number
  }

  type getUserByIdParams = {
    id: number
  }

  type getUserVoByIdParams = {
    id: number
  }

  type PageResultUserVo = {
    pageNo?: number
    pageSize?: number
    total?: number
    totalPages?: number
    records?: UserVo[]
  }

  type ResultBoolean = {
    code?: number
    data?: boolean
    message?: string
  }

  type ResultLong = {
    code?: number
    data?: number
    message?: string
  }

  type ResultPageResultUserVo = {
    code?: number
    data?: PageResultUserVo
    message?: string
  }

  type ResultUser = {
    code?: number
    data?: User
    message?: string
  }

  type ResultUserVo = {
    code?: number
    data?: UserVo
    message?: string
  }

  type User = {
    id?: number
    userAccount?: string
    userPassword?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    editTime?: string
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type UserAddDto = {
    userName?: string
    userAccount?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
  }

  type UserLoginDto = {
    userAccount?: string
    userPassword?: string
  }

  type UserQueryDto = {
    pageNo?: number
    pageSize?: number
    id?: number
    userName?: string
    userAccount?: string
    userProfile?: string
    userRole?: string
  }

  type UserRegisterDto = {
    userAccount?: string
    userPassword?: string
    checkPassword?: string
  }

  type UserUpdateDto = {
    id?: number
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    createTime?: string
  }

  type UserVo = {
    id?: number
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    createTime?: string
  }
}
