

const toggleSideBar=()=>{
    
    if($(".sidebar").is(":visible")){
        
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");
        
    }else{
        
         $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");
        
        
    }
    
    
};

$(document).ready(function(){
  $('[data-toggle="delete"]').tooltip();   
});

$(document).ready(function(){
  $('[data-toggle="update"]').tooltip();   
});