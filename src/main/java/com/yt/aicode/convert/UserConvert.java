package com.yt.aicode.convert;

import com.yt.aicode.basic.entity.User;
import com.yt.aicode.dto.UserAddDto;
import com.yt.aicode.dto.UserLoginDto;
import com.yt.aicode.dto.UserQueryDto;
import com.yt.aicode.dto.UserUpdateDto;
import com.yt.aicode.vo.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author wys17
 */
@Mapper
public interface UserConvert {
    UserConvert INSTANCE= Mappers.getMapper(UserConvert.class);
    User loginToY(UserLoginDto userLoginDto);
    UserVo yToVo(User user);
    User addToY(UserAddDto uerAddDto);
    User updateToY(UserUpdateDto userUpdateDto);
    User queryDtoToY(UserQueryDto userQueryDto);
}