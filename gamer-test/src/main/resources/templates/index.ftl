<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
    xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">

<head>
    <meta charset="UTF-8">
    <title th:text="${title}">首页-Easyblog</title>
    <meta name="keywords" content="blog" />
    <meta name="description" content="blog" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link th:href="@{~/css/meterial_icons.css}" href="/css/meterial_icons.css" rel="stylesheet">
    <link th:href="@{~/css/bootstrap.css}" href="/css/bootstrap.min.css" rel="stylesheet">
    <!-- Material Design Bootstrap -->
    <link th:href="@{~/css/mdb.min.css}" href="/css/mdb.min.css" rel="stylesheet">
    <!-- Your custom styles (optional) -->
    <link th:href="@{~/css/style.css}" href="/css/style.css" rel="stylesheet">
</head>

<body class="mdbootstrap">
    <header>
        <nav class="navbar fixed-top navbar-expand-md navbar-light white double-nav scrolling-navbar top-nav-collapse">

            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#collapseContent"
                aria-controls="basicExampleNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="navbar-brand" href="/index">
                Easyblog
            </div>
            <div class="collapse navbar-collapse" id="collapseContent">
                <ul class="navbar-nav mr-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="/index">首页
                            <span class="sr-only">(current)</span>
                        </a>
                    </li>

                    <!--<li class="nav-item">
                        <a class="nav-link" href="#">前端</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">后端</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">Android</a>
                    </li>-->
                </ul>
            </div>

            <ul class="nav navbar-nav nav-flex-icons ml-auto">
                <li class="nav-item">
                    <!--<form class="form-inline mr-auto">
                        <input class="form-control" type="text" placeholder="Search" aria-label="Search">
                        <button id="search-control" class="btn btn-mdb-color btn-rounded btn-sm my-0 ml-sm-2"
                            type="button">Search</button>
                    </form>-->
                    <a href="#" class="nav-link" id="search-control">
                        <i class="align-middle material-icons">search</i></a>
                    <div class="search-wrap">
                        <div id="search-content" class="search-content">
                            <input id="search-input" type="text" placeholder="请输入您要搜索的关键词"
                                class="search-input form-control">
                        </div>
                    </div>
                </li>
                <li id="logout-drop-meun" class="nav-item dropdown">
                    <a class="nav-link" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true"
                        aria-expanded="false">
                        <i class="material-icons">person</i>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right dropdown-default"
                        aria-labelledby="navbarDropdownMenuLink">
                        <a class="dropdown-item" href="/login">登录</a>
                        <a class="dropdown-item" href="/signup">注册</a>
                    </div>
                </li>

                <li id="login-drop-menu" class="nav-item dropdown">
                    <a class="nav-link" id="navbarDropdownMenuLink1" data-toggle="dropdown" aria-haspopup="true"
                        aria-expanded="false">
                        <img id="head-pic" src="/img/svg/default-avatar.svg"
                            class="img-fluid z-depth-1 rounded-circle head-photo">
                        <!--<i class="material-icons">person</i> -->
                    </a>
                    <div class="dropdown-menu dropdown-menu-right dropdown-default"
                        aria-labelledby="navbarDropdownMenuLink1">
                        <a class="dropdown-item" href="/login">写文章</a>
                        <a class="dropdown-item" href="/login">我的主页</a>
                        <a class="dropdown-item" href="/login">帐号设置</a>
                        <a class="dropdown-item" href="/logout">退出登录</a>
                    </div>
                </li>

            </ul>
        </nav>
        <div class="card card-intro sky-gradient">
            <div class="card-body white-text rgba-black-light text-center">
                <!--Grid row-->
                <div class="row d-flex justify-content-center">
                    <!--Grid column-->
                    <div class="col-md-6">
                    </div>
                    <!--Grid column-->
                </div>
                <!--Grid row-->
            </div>
        </div>
    </header>
    <main class="pt-5">
        <div layout:fragment="content" id="content" class="container-fluid">

        </div>
    </main>


    <script th:src="@{~/js/jquery-3.2.1.min.js}" src="/js/jquery-3.2.1.min.js"></script>
    <script th:src="@{~/js/bootstrap.min.js}" src="/js/bootstrap.min.js"></script>
    <script th:src="@{~/js/popper.js}" src="/js/popper.js"></script>
    <script th:src="@{~/js/mdb.min.js}" src="/js/mdb.min.js"></script>
    <script th:src="@{~/js/base.js}" src="/js/base.js"></script>
    <script layout:fragment="extrajs"></script>
    <script>
        $(document).ready(function () {
            $('#login-drop-menu').hide();
            $.ajax({
                url: '/api/member/curmem',
                dataType: 'json',
                success: function (message) {
                    if (message.state == 'success') {
                        console.log(message.data);
                        user = message.data;
                        $('#head-pic').attr('src', 'images/' + user.headPic);
                        $('#logout-drop-meun').hide();
                        $('#login-drop-menu').show();
                    }
                }
            });

            $('#search-control').click(function (event) {
                if ($('#search-content').hasClass('open')) {
                    sv = $('#search-input').val();
                    if (sv == '') {
                        $('#search-content').removeClass('open');
                    } else {
                        // 搜索
                    }
                } else {
                    $('#search-input').focus();
                    $('#search-content').addClass('open');
                }
            });

            $('#search-input').blur(function (event) {
                event.preventDefault();
                sv = $('#search-input').val();
                if (sv == '') {
                    $('#search-content').removeClass('open');
                }
                return false;
            });

        });
    </script>


</body>

</html>