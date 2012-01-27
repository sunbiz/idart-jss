#!/usr/bin/perl -w
#  
#  Copyright (c) 2002 Steve Slaven, All Rights Reserved.
#  
#  This program is free software; you can redistribute it and/or
#  modify it under the terms of the GNU General Public License as
#  published by the Free Software Foundation; either version 2 of
#  the License, or (at your option) any later version.
#  
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
#  GNU General Public License for more details.
#  
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software
#  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
#  MA 02111-1307 USA
#  

# Adds a license header to a file, unless it detects one already present
# can also recognize a few types of files and tries to add the license
# as proper comments
use strict;
use vars qw( %ENV );

use Getopt::Std;
use File::Copy;

my %licenses = (
	gpl2 => q{Copyright (c) [%year%] [%author%], All Rights Reserved.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 2 of
the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston,
MA 02111-1307 USA},
	perl => q{Copyright (c) [%year%] [%author%]. All rights reserved.

This program is free software  and is provided "as is" without
express or implied warranty. You can redistribute it and/or
modify it under the same terms as Perl itself.},
	mpl => q{Copyright (c) [%year%] [%author%]. All rights reserved.

The contents of this file are subject to the Mozilla Public
License Version 1.1 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of
the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS
IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
implied. See the License for the specific language governing
rights and limitations under the License.},
	consultant => q{Copyright (c) [%year%] [%author%]. All rights reserved.

The contents of this file are provided under a nontransferrable,
nonexclusive license.  This license may be terminated at any
time without reason, notice, or consent, at which time all
copies of the file must be removed including but not limited
to copies in production use, on backup media, or in print.  This
license shall be terminated in the event the Licensee fails to
comply to the terms of this license.

This file may not be reproduced in part or in whole without the
express written consent of the copyright holder, including but
not limited to distribution to affiliates, internal developers,
3rd parties, or published in print or electronically.

SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THOSE
RELATING TO MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. 

LIMITATION OF LIABILITY: LICENSOR SHALL NOT BE LIABLE FOR ANY
LOSS OF PROFITS, LOSS OF USE, LOSS OF DATA, INTERRUPTIONS
OF BUSINESS, NOR FOR INDIRECT, SPECIAL, INCIDENTAL OR CONSEQUENTIAL
DAMAGES OF ANY KIND WHETHER UNDER THIS AGREEMENT OR OTHERWISE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.

No rights are granted to the Licensee that are not explicitly
stated in this license unless contained in a supplemental
written contract signed by both parties.} );

# Regex to detect if a license is in a file
my %detect = (
	gpl2 => 'GNU General',
	perl => 'same terms as Perl itself',
	mpl => 'Mozilla Public',
	consultant => 'under a nontransferrable,'
);

# Subs to commentify for languanges
my %commentify = (
	perl => \&comment_hash,
	sh => \&comment_hash,
	c => \&comment_c,
	cpp => \&comment_cpp,
	text => \&comment_text
);

# Subs to handle inserting
my %insertify = (
	perl => \&insert_line2,
	sh => \&insert_line2,
	c => \&insert_line1,
	cpp => \&insert_line1,
	text => \&insert_line1,
);

# Output of 'file' command to autodetect file types
my %autodetect = (
	perl => 'perl script',
	text => '(text|data)',
	c => 'C program text',
	cpp => 'C++ program text',
	sh => 'shell script'
);

my %extensions = (
	pm => 'perl',
	pl => 'perl',
	c => 'c',
	cpp => 'cpp',
	sh => 'sh'
);

my %o;

# Handle defaults
$o{ l } = 'gpl2';
$o{ s } = 3;
$o{ a } = $ENV{ USER };

# Load rc
my %kwswitch = (
	author => 'a',
	license => 'l',
	language => 'L',
	space => 's',
	force => 'f'
);

if( open( IN, "$ENV{HOME}/.add_licenserc" ) ) {
	my $line;
	my( $kw, $val );
	my( @splitup );
	while( $line = <IN> ) {
		chomp( $line );
		$line =~ s/#.*$//;
		$line =~ s/^\s*//;
		( $kw, $val ) = $line =~ /^(\S+)\s*=\s*(.*)/;
		$val =~ s/\s+$// if $val;

		if( $kw ) {
			if( $kwswitch{ $kw } ) {
				$o{ $kwswitch{ $kw } } = $val;
			}else{
				# Maybe it's a 'license' key?
				if( $kw eq 'newlicense' ) {
					# Yep, read in the license
					@splitup = split( /\t/, $val );
					$splitup[ 2 ] = '' unless $splitup[ 2 ];
					print "Adding license: $splitup[0]\n";
					print " from $splitup[1]\n";
					print " detected by '$splitup[2]'\n";

					$splitup[ 1 ] =~ s/^~/$ENV{HOME}/;

					open( NEWLICENSE, $splitup[1] ) ||
						warn( "Unable to open '$splitup[1]': $!" );
					$licenses{ $splitup[ 0 ] } =
						join( '', <NEWLICENSE> );
					close( NEWLICENSE );
					$detect{ $splitup[ 0 ] } = 
						$splitup[ 2 ];
				}else{
					warn( "Unknown keyword: '$kw'" );
				}
			}
		}
	}
	close( IN );
}

# Get cmdline
getopts( 'hfL:l:s:a:', \%o );

# Load substitutions
my %subs = (
	year => ( localtime() )[ 5 ] + 1900,
	author => $o{ a }
);

# Force help unless files
$o{ h } = 1 unless $ARGV[ 0 ];
die( usage() ) if $o{ h };

# Handle unknown
die( "Unknown license '$o{l}'" ) unless defined( $licenses{ $o{ l } } );

# Check if already present and detect languages
my @files;
my( $file, $lang, $lic, $force );
for $file ( @ARGV ) {
	# Handle lang/lic parse
	$lang = $lic = $force = undef;
	if( $file =~ /^!/ ) {
		$file =~ s/^.//;
		$force = 1;
	}
	if( $file =~ /:/ ) {
		( $lic, $file ) = $file =~ /^(\S+?):(.*)/;
		if( $lic =~ /,/ ) {
			# Grab language
			( $lic, $lang ) = split( /,/, $lic );
		}
	}

	push( @files, {
		file => $file,
		lang => $lang,
		lic => $lic || $o{ l },
		force => $force || $o{ f }
	} );

	# Check if file has thing
	if( -f $file ) {
		if( open( IN, $file ) ) {
			if( ! defined( $licenses{ $files[ -1 ] -> { lic } } ) ) {
				warn( sprintf( "Unknown license: '%s'",
					$files[ -1 ] -> { lic } ) );
				@files = remove_file( $file, @files );
			}elsif( grep( /\Q$detect{ $files[ -1 ] -> { lic } }\E/,
						 <IN> ) &&
					( ! $files[ -1 ] -> { force } ) ) {
				warn( sprintf( "'%s' already has license '%s'",
					$file,
					$files[ -1 ] -> { lic } ) );
				@files = remove_file( $file, @files );
			}else{
				# Handle lang detect if needed
				$files[ -1 ] -> { lang } = $o{ L }
					if $o{ L };

				if( ! $files[ -1 ] -> { lang } ) {
					$files[ -1 ] -> { lang } =
						detect_lang( $files[ -1 ] ->
							{ file } );
				}

				# Verify known language
				if( ! $commentify{ 
						$files[ -1 ] -> { lang } } ) {
					warn( sprintf( "Unknown languange '%s'",
						$files[ -1 ] -> { lang } ) );
					@files = remove_file( $file, @files );
				}
			}
			close( IN );
		}else{
			warn( "Unable to open '$file': $!" );
			@files = remove_file( $file, @files );
		}
	}else{
		print "File '$_' does not exist\n";
		@files = remove_file( $file, @files );
	}
}

die( "No files to process" ) unless scalar( @files ) > 0;

# Make commented
my $insert;
for( @files ) {
	# Notify the user
	printf( 'Updating: "%s" as "%s" with "%s"',
		$_ -> { file },
		$_ -> { lang },
		$_ -> { lic } );
	print "\n";

	# Get initial commented license
	$insert = &{ $commentify{ $_ -> { lang } } }( $_ -> { lic } );

	# Do subs
	$insert =~ s/\[%(.*?)%\]/$subs{$1}/ge;

	# Handle insertion
	$file = $_ -> { file };
	copy( $file, "$file.tmp" );
	&{ $insertify{ $_ -> { lang } } }( "$file.tmp", $file, $insert );
	unlink( "$file.tmp" );
}


# Commenting subs
sub comment_hash {
	# Makes it perl commentish, or shell
	return( comment_generic( "#", shift() ) );
}

sub comment_cpp {
	return( comment_generic( "//", shift() ) );
}

sub comment_text {
	return( comment_generic( "", shift() ) );
}

sub comment_c {
	return( "/*\n" . 
		comment_generic( " *", shift() ) .
		"*/\n" );
}

sub comment_generic {
	# Takes a preceding comment char, and commentifies the license
	my $char = shift;
	my $license = shift;

	my $ret;

	for( split( /\n/, get_license( $license ), -1 ) ) {
		$ret .= $char . " " x $o{ s } . $_ . "\n";
	}

	return( $ret );
}

sub get_license {
	return( "\n" . $licenses{ shift() } . "\n" );
}

# Insertification
sub insert_line1 {
	# For c/cpp/text/etc where license starts at line 1
	my $input = shift;
	my $output = shift;
	my $insert = shift;
	local *OUT;
	local *IN;

	open( IN, $input ) || die( "Couldn't open '$input': $!" );
	open( OUT, ">$output" ) || die( "Couldn't open '$output': $!" );

	print OUT $insert;
	print OUT <IN>;

	close( OUT );
	close( IN );
}

sub insert_line2 {
	# Used for shell scripts, etc, where license should be
	# right after the first line
	my $input = shift;
	my $output = shift;
	my $insert = shift;
	local *OUT;
	local *IN;

	open( IN, $input ) || die( "Couldn't open '$input': $!" );
	open( OUT, ">$output" ) || die( "Couldn't open '$output': $!" );

	print OUT scalar( <IN> );
	print OUT $insert;
	print OUT <IN>;

	close( OUT );
	close( IN );
}

sub usage {
	my $usage = qq{
add_license v1.30
Adds license notification to a source file
Author: Steve Slaven - http://hoopajoo.net

Usage: $0 [-hf] [-s whitespace] [-l default license] [-L force language]
		[!license,language:]file [ file ... ]

	-h	This help
	-f	Force insertion even if it looks like the license is
		already in place for all files
	-s	Number of spaces between comments and start of license
	-l	Default license when not explicitly specified
	-L	Force language, all files will be assumed to be this type

	!	Force insertion for this file only
	lang	Language to use when commentifying the license (see below)
	license	License to include (see below)

add_license does rudementory checks to see if the license is already
in place, and if not, will modify the file to include the requested
license.

Configuration options can also be placed in a HOME/.add_licenserc
in the format:

keyword = value

With the following keywords equal to the following switches:

        author => 'a'
        license => 'l'
        space => 's'

New licenses can be added with the newlicense keyword, followed
by the name, filename, and detect string which MUST BE seperated
by TABS.

So a simple RC might contain:

-- begin --
# Set common defaults here
author = Steve Slaven
space = 2
license = gpl2
newlicense = my_license	~/licenses/my_license	Licensed under MY LICENSE
--  end  --

The last part of the my_license thing is used to detect if the
license has already been added.  In the license file special
keyword sequences can be used to input special data, such as date
or author.  They are enclosed in \[\%keyword\%\] sequences.

	author		The author as defined in the RC file
	year		Current 4 digit year

};

	$usage .= "Licenses:\n";
	for( keys( %licenses ) ) {
		$usage .= "   $_\n";
	}

	$usage .= "\nLanguages:\n";
	for( keys( %commentify ) ) {
		$usage .= "   $_\n";
	}

	# Print current opts
	$usage .= "\nCurrent Options:\n";
	$usage .= join( "\n", map { " $_ => $o{$_}" } keys( %o ) );
	$usage .= "\n\nCurrent Subs (keyword seqs):\n";
	$usage .= join( "\n", map { " $_ => $subs{$_}" } keys( %subs ) );

	$usage .= "\n";
	return( $usage );
}

# Returns all but $1
sub remove_file {
	my $file = shift;
	my @files;

	for( @_ ) {
		if( $_ -> { file } ne $file ) {
			push( @files, $_ );
		}
	}

	return( @files );
}

# Tries to detect language type
sub detect_lang {
	my $file = shift;

	# Uses 'file'
	my $is = `file "$file"`;
	for( keys( %autodetect ) ) {
		if( $is =~ /\Q$autodetect{$_}\E/ ) {
			return( $_ );
		}
	}

	# Try by extension
	my $ext;
	( $ext ) = $file =~ /\.([a-zA-Z0-9]+)$/;
	return( $extensions{ $ext } ) if $extensions{ $ext };

	return( 'unknown' );
}
