xquery version "1.0-ml";

declare variable $input external;

(:
module namespace search="http://marklogic.com/search";
:)

declare function local:hello($who as xs:string) as xs:string {
  if ($who eq "")
  then
    'Piss off'
  else
    fn:concat('Hello ', $who)
};

declare function local:hello($who as xs:string, $count as xs:integer) as item() {
(:    [1 to $count] :)
    <x>$who</x>
};

let $x := xdmp:log("prdel")

return local:hello($input,2);
