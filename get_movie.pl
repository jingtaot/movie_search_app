#!/usr/bin/perl -w
print"Content-type:text/html\n\n";
use LWP::Simple;
#Add a header directive indicating that this is encoded in UTF-8
#print"<meta http-equiv=\"Content-Type\"content=\"text/html;charset=UTF-8\"/>\n";

#post or get
if($ENV{'REQUEST_METHOD'} eq "GET")
  {
		$buffer = $ENV{'QUERY_STRING'};
	}
else
	{
		read(STDIN, $buffer, $ENV{'CONTENT_LENGTH'});
	}
	

@pairs=split(/&/,$buffer);
$movie=@pairs[0];
($movie_name,$movie_value)=split(/=/,$movie);


#when the movie input is null
while($movie_value eq "")
	{
		$url="http://www-scf.usc.edu/~jingtaot/hw6.html";
		$null=get($url);
		print $null;
		exit;
	}

#Check whether LWP module is installed
if(eval{require LWP::Simple;}){
}else{
print"You need to install the Perl LWP module<br>";
exit;
}

$media=@pairs[1];
($media_name,$media_value)=split(/=/,$media);
@webmedia=split(/\+/,$media_value);
$webmedia[0]=~tr/[A-Z]/[a-z]/;

#Retrieve the content of an URL
$url="http://www.imdb.com/search/title?title=$movie_value&title_type=$webmedia[0]";
$content=LWP::Simple::get($url);
#remove \n
#chomp($content);   				
$content=~s/\n//g;
$content=~s/\s+/ /g;
#part
@partset=($content=~/\<td class="number"\>.*?Dir: \<a href=".*?\<\/a\>/g);
#image
@imageset=($content=~/\<td class="image"\>(.*?)\<\/td\>/g);
#title
@titleset=($content=~/"search"\>\<\/span\>(.*?)\<\/a\>/g);
print"#@titleset";
#year
@yearset=($content=~/\<span class="year_type"\>(.*?)\<\/span\>/g);
#director
@directorset=($content=~/\<span class="credit"\>(.*?)\<\/span\>/g);
#rating
@ratingset=($content=~/\<span class="value"\>(.*?)\<\/span\>/g);
#link
@linkingset=($content=~/\<td class="image"\>(.*?)"\>\<\/a\>/g);
#number
@numberset=($content=~/\<td class="number"\>(.*?)\<td class="title"\>/g);



$movie_string=$movie_value;
$movie_string=~s/\W+/ /g;
$media_string=$media_value;
$media_string=~s/\W+/ /g;
if($titleset[0] eq "")
	{
		print"<html><head>\n";
# Add a header directive indicating that this is encoded in UTF-8  
		print "<meta http-equiv=\"Content-Type\" content=\"text/html;  
charset=UTF-8\"/>\n";	
		print"<title>Search Result</title>\n";
		print"</head><body>\n";
		print"<h1><center>Search Result</center></h1>\n";
		print"<p><center>\"$movie_string\" of type \"$media_string\":</center></p>\n";
		print"<h1><center>No movies found!</center></h1>\n";
		print"</body></html>";
	}
else
	{
		print"<html><head>\n";
# Add a header directive indicating that this is encoded in UTF-8  
		print "<meta http-equiv=\"Content-Type\" content=\"text/html;  
charset=UTF-8\"/>\n";
		print"<title>Search Result</title>\n";
		print"</head><body>\n";
		print"<h1><center>Search Result</center></h1>\n";
		print"<p1><center>\"$movie_string\" of type \"$media_string\":</center></p>\n";
		print"<table border='1' align=center>\n";
		print"<tr><th>Image</th><th>Title</th><th>Year</th><th>Director</th><th>Rating<br>(10)</th><th>Link to Movie</th></tr>\n";
		
#		print"@titleset";
		for($i=0;$i<=$#titleset&&$i<=4;$i++)
			{ 	
				#get image
				@imagelist=($numberset[$i]=~/"\>\<img src="(.*?)" height=/);
				#$image=$imagelist[0];
				print "<tr><td><img src='$imagelist[0]' width='74' height='54'/></td>";
				
				#get title
				@titlelist=($numberset[$i]=~/" title="(.*?) \(/);
				print "<td><center>$titlelist[0]</center></td>";
				
				#get year
				@yearlist=($yearset[$i]=~/\((.*?)\)/);
				print "<td><center>$yearlist[0]</center></td>";
				
				#get director
				@directorlist[$i]=($partset[$i]=~/Dir: \<a href=".*?"\>(.*?)\<\/a\>/);
				if($directorlist[$i] eq "")
					{
						print"<td><center>N/A</center></td>";
					}
				else
					{
						print "<td><center>$directorlist[$i]</center></td>";
					}
				#@directorlist=($directorset[$i]=~/"\>(.*?)\<\/a\>\s+With:/);
				#print "<td><center>$directorlist[0]</center></td>";
				
				#get rating
				@ratinglist[$i]=($partset[$i]=~/\<span class="value"\>(.*?)\<\/span\>/);
				if($ratinglist[$i] eq "")
					{
						print"<td><center>N/A</center></td>";
					}
				else
					{
						print "<td><center>$ratinglist[$i]</center></td>";
					}
				#get link
				#@linkingset=($content=~/data-caller-name="search"\>\<\/span\>(.*?)\<a href="(.*?)"\>/);
				#print"@linkingset";
				@linkinglist=($linkingset[$i]=~/\<a href="(.*?)" title="/);
				#print"@linkinglist";
				$linking="http://www.imdb.com$linkinglist[0]";
				#print"$linking";
				print "<td><a href='$linking'><center>details</center></a></td></tr>";
			}
		
		print "</table></body></html>";				
	}
