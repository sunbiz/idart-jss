#! /bin/csh
foreach FILE ( `ls *` )
	echo $FILE
        convert $FILE -filter lanczos -resize 600x400\> -unsharp 0x1 $FILE
end
