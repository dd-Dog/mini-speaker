#定义cmake支持的最小版本号
cmake_minimum_required(VERSION 3.4.1)


add_library( # 设置生成so库的文件名称，例如此处生成的so库文件名称应该为：native-lib.so
             native-lib

             # 设置生成的so库类型，类型只包含两种：
             # STATIC:静态库，为目标文件的归档文件，在链接其他目标的时候使用
             # SHARED:动态库，会被动态链接，在运行时被加载
             SHARED

             # 设置源文件的位置，可以是很多个源文件，都要添加进来，注意相对位置
             src/main/cpp/NativeHelper.cpp
             src/main/cpp/des.cpp)

# 从系统里查找依赖库，可添加多个
find_library( # 例如查找系统中的log库liblog.so
              log-lib

              # liblog.so库指定的名称即为log,如同上面指定生成的libnative-lib.so库名称为native-lib一样
              log )
# 配置目标库的链接，即相互依赖关系
target_link_libraries( # 目标库（最终生成的库）
                       native-lib

                        # 依赖于log库，一般情况下，如果依赖的是系统中的库，需要加 ${} 进行引用，
                        # 如果是第三方库，可以直接引用库名，例如：
                        # 引用第三方库libthird.a，引用时直接写成third;注意，引用时，每一行只能引用一个库
                       ${log-lib} )
