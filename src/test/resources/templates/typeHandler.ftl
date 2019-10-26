package ${packageName};

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

<#if isNeedImport>
import ${importClass};
</#if>

public class ${type}TypeHandler extends AbstractTypeHandler<${realType}> {
	
	@Override
	protected void setNotNullParameter(PreparedStatement stat, int pos, ${realType} parameter) throws SQLException {
		<#if type?ends_with('s')>
		stat.set${type}(pos, parameter);
		<#else>
		stat.set${type}(pos, parameter);
		</#if>
	}
	
	@Override
	protected ${realType} getNotNullResult(ResultSet rs, int columnIndex) throws SQLException {
		return rs.get${type}(columnIndex);
	}

	@Override
	protected ${realType} getNotNullResult(ResultSet rs, String columnName) throws SQLException {
		return rs.get${type}(columnName);
	}

}