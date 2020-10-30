<h1>
    ${data.getConfig("title", "Dude!")}
</h1>
<body>
<table border="1" align="center">
    <thead>
    <#list data.getColumns() as col>
        <th>${col?upper_case}</th>
    </#list>
    </thead>
    <tbody>
    <#list data.getData() as item>
        <tr>
            <#list data.getColumns() as col>
                <td>${item[col]}</td>
            </#list>
        </tr>
    </#list>
    </tbody>
</table>
<footer>
    ${data.getConfig("footer", "No footer found!")}
</footer>
</body>

