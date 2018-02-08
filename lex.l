%{

%}

ws          [\t\n]+
letter      [a-zA-Z]
digit       [0-9]
id          {letter}({letter}|{digit})*
number      {digit}+
decimal     (({digit}+\.{digit}*)|({digit}*\.{digit}+))

%%

">"         {token("relop");}
">="        {token("relop");}
"=="        {token("relop");}
"<"         {token("relop");}
"<="        {token("relop");}
"+"         {token("operator");}
"-"         {token("operator");}
"*"         {token("operator");}
"/"         {token("operator");}
"="         {token("operator");}
" "         {System.out.println("white space");}
{ws}        {/* do nothing */}
int         {token("int");}
float       {token("float");}
{id}        {token("id");}
{number}    {token("number");}
{decimal}   {token("decimal");}
if          {token("if");}
else        {token("else");}
while       {token("while");}
for         {token("for");}

%%
public static void token(String tag){
    System.out.println("<"+text+", "+tag+">");
}
