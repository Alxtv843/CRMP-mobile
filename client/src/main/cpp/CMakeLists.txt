cmake_minimum_required(VERSION 3.22.1)
project(crmp_client)

add_library(crmp_client SHARED
    native_client.cpp
)

find_library(log-lib log)

target_link_libraries(crmp_client
    ${log-lib}
)

target_compile_options(crmp_client PRIVATE
    -Wall
    -Wextra
)
