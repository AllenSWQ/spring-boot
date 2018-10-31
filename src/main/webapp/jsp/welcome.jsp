<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="webjars/jquery/2.1.4/jquery.min.js"></script>
<title>Springboot JSP</title>
<script>  
    $(function(){  
        $('#title').click(function(){  
            alert($(this).text());  
        });  
    })  
</script>
</head>
<body>
	<center>
		<h1 id="title">hello springboot</h1>
	</center>
</body>
</html>