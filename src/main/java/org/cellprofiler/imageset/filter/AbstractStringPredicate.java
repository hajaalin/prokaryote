package org.cellprofiler.imageset.filter;

import org.cellprofiler.imageset.filter.Filter.BadFilterExpressionException;

import java.util.List;

/**
 * @author Lee Kamentsky
 *
 * A filter predicate that evaluates a string, optionally using a literal for the comparison.
 */
public abstract class AbstractStringPredicate implements
		FilterPredicate<String, String> {
	private String literal;

	public Class<String> getInputClass() {
		return String.class;
	}
	public Class<String> getOutputClass() {
		return null;
	}
	/* (non-Javadoc)
	 * @see org.cellprofiler.imageset.filter.FilterPredicate#setSubpredicates(org.cellprofiler.imageset.filter.FilterPredicate<TOUT,?>[])
	 */
	public void setSubpredicates(List<FilterPredicate<String, ?>> subpredicates) throws BadFilterExpressionException {
		throw new AssertionError("String predicates use literals, not subpredicates.");
	}
	/* (non-Javadoc)
	 * @see org.cellprofiler.imageset.filter.FilterPredicate#setLiteral(java.lang.String)
	 */
	public void setLiteral(String literal) throws BadFilterExpressionException {
		this.literal = literal;
	}
	/* (non-Javadoc)
	 * @see org.cellprofiler.imageset.filter.FilterPredicate#eval(java.lang.Object)
	 */
	public boolean eval(String candidate) {
		return eval(candidate, literal);
	}
	
	/**
	 * Compare the candidate against a literal.
	 * 
	 * @param candidate the candidate to be evaluated
	 * @param literal a literal value used in the comparison
	 * @return true to pass, false to filter out
	 */
	abstract protected boolean eval(String candidate, String literal);
}
